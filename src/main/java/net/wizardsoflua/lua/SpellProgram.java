package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static net.wizardsoflua.WizardsOfLua.LOGGER;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.inject.Inject;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.TableFactory;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.env.RuntimeEnvironment;
import net.sandius.rembulan.env.RuntimeEnvironments;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.Continuation;
import net.sandius.rembulan.impl.StateContexts;
import net.sandius.rembulan.lib.BasicLib;
import net.sandius.rembulan.lib.CoroutineLib;
import net.sandius.rembulan.lib.IoLib;
import net.sandius.rembulan.lib.MathLib;
import net.sandius.rembulan.lib.ModuleLib;
import net.sandius.rembulan.lib.StringLib;
import net.sandius.rembulan.lib.TableLib;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.GameProfiles;
import net.wizardsoflua.TimeService;
import net.wizardsoflua.config.WolConfig;
import net.wizardsoflua.extension.ExtensionLoader;
import net.wizardsoflua.extension.InjectionScope;
import net.wizardsoflua.extension.SpellScope;
import net.wizardsoflua.extension.api.inject.PostConstruct;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.ParallelTaskFactory;
import net.wizardsoflua.extension.spell.api.resource.Config;
import net.wizardsoflua.extension.spell.api.resource.ExceptionHandler;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.api.resource.LuaTypes;
import net.wizardsoflua.extension.spell.api.resource.ScriptGatewayConfig;
import net.wizardsoflua.extension.spell.api.resource.Spell;
import net.wizardsoflua.extension.spell.api.resource.Time;
import net.wizardsoflua.extension.spell.spi.JavaToLuaConverter;
import net.wizardsoflua.extension.spell.spi.LuaToJavaConverter;
import net.wizardsoflua.filesystem.WolServerFileSystem;
import net.wizardsoflua.lua.compiler.PatchedCompilerChunkLoader;
import net.wizardsoflua.lua.dependency.ModuleDependencies;
import net.wizardsoflua.lua.dependency.ModuleDependency;
import net.wizardsoflua.lua.module.events.EventsModule;
import net.wizardsoflua.lua.module.luapath.AddPathFunction;
import net.wizardsoflua.lua.module.print.PrintRedirector;
import net.wizardsoflua.lua.module.print.PrintRedirector.PrintReceiver;
import net.wizardsoflua.lua.module.searcher.ClasspathResourceSearcher;
import net.wizardsoflua.lua.module.searcher.LuaFunctionBinaryCache;
import net.wizardsoflua.lua.module.searcher.PatchedChunkLoadPathSearcher;
import net.wizardsoflua.lua.module.spell.SpellModule;
import net.wizardsoflua.lua.module.spell.SpellsModule;
import net.wizardsoflua.lua.module.types.Types;
import net.wizardsoflua.lua.scheduling.CallFellAsleepException;
import net.wizardsoflua.lua.scheduling.LuaScheduler;
import net.wizardsoflua.lua.view.ViewFactory;
import net.wizardsoflua.profiles.Profiles;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.spell.SpellException;
import net.wizardsoflua.spell.SpellExceptionFactory;
import net.wizardsoflua.spell.SpellRegistry;

public class SpellProgram {
  public static final String ROOT_CLASS_PREFIX = "SpellByteCode";

  private final World world;
  private @Nullable Entity owner;
  private final PrintReceiver printReceiver;
  private final String code;
  private final ImmutableList<String> arguments;

  private final StateContext stateContext;
  private final LuaScheduler scheduler;
  private final Table env;
  private final RuntimeEnvironment runtimeEnv;
  private final PatchedCompilerChunkLoader loader;
  private final SpellExceptionFactory exceptionFactory;
  private final Collection<ParallelTaskFactory> parallelTaskFactories = new ArrayList<>();

  @Resource
  private InjectionScope parentScope;
  @Inject
  private GameProfiles gameProfiles;
  @Inject
  private LuaFunctionBinaryCache luaFunctionCache;
  @Inject
  private Profiles profiles;
  @Inject
  private SpellRegistry spellRegistry;
  @Inject
  private TimeService time;
  @Inject
  private WolConfig config;
  @Inject
  private WolServerFileSystem fileSystem;

  private String luaPath;
  private ModuleDependencies dependencies;
  private SpellScope spellScope;

  private SpellEntity spellEntity;

  /**
   * The totalWorldTime at which this program should stop sleeping.
   */
  private long wakeUpTime;
  private Continuation continuation;
  private State state = State.NEW;

  private enum State {
    NEW, SLEEPING, PAUSED, FINISHED, TERMINATED;
  }

  SpellProgram(World world, @Nullable Entity owner, PrintReceiver printReceiver, String code,
      String... arguments) {
    this.world = requireNonNull(world, "world");
    this.owner = owner;
    this.printReceiver = requireNonNull(printReceiver, "printReceiver");
    this.code = requireNonNull(code, "code");
    this.arguments = ImmutableList.copyOf(arguments);

    stateContext = StateContexts.newDefaultInstance();
    scheduler = new LuaScheduler(stateContext);
    env = stateContext.newTable();
    runtimeEnv = RuntimeEnvironments.system();
    loader = PatchedCompilerChunkLoader.of(ROOT_CLASS_PREFIX);
    exceptionFactory = new SpellExceptionFactory();
  }

  @PostConstruct
  private void postConstruct() {
    luaPath = getDefaultLuaPath(owner);
    dependencies = createDependencies(owner);
    spellScope = createSpellScope();
    installSystemLibraries();
    AddPathFunction.installInto(env, getConverters(), new AddPathFunction.Context() {
      @Override
      public String getLuaPathElementOfPlayer(String nameOrUuid) {
        UUID uuid = gameProfiles.getUuid(nameOrUuid);
        if (uuid == null) {
          throw new IllegalArgumentException(format("Player not found with name '%s'", nameOrUuid));
        }
        return config.getOrCreateWizardConfig(uuid).getLibDirPathElement();
      }

      @Override
      public void addPath(String pathelement) {
        luaPath += ";" + pathelement;
      }
    });
    PrintRedirector.installInto(env, printReceiver);
  }

  private String getDefaultLuaPath(Entity owner) {
    if (owner instanceof EntityPlayer) {
      return config.getSharedLuaPath() + ";"
          + config.getOrCreateWizardConfig(owner.getUniqueID()).getLibDirPathElement();
    }
    return config.getSharedLuaPath();
  }

  private ModuleDependencies createDependencies(Entity owner) {
    ModuleDependencies result = new ModuleDependencies();
    result.add(new ModuleDependency("wol.Globals"));
    result.add(new ModuleDependency("wol.inspect"));
    result.add(new ModuleDependency("wol.Check"));
    result.add(new ModuleDependency("wol.Object"));
    result.add(new ModuleDependency("wol.Vec3"));
    result.add(new ModuleDependency("wol.Material"));
    result.add(new ModuleDependency("wol.Block"));
    result.add(new ModuleDependency("wol.Entity"));
    result.add(new ModuleDependency("wol.Spell"));
    result.add(new ModuleDependency("wol.Player"));

    String sharedProfile = profiles.getSharedProfile();
    if (sharedProfile != null) {
      result.add(new ModuleDependency(sharedProfile));
    }
    if (owner instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) owner;
      String playerProfile = profiles.getProfile(player);
      if (playerProfile != null) {
        result.add(new ModuleDependency(playerProfile));
      }
    }
    return result;
  }

  /**
   * Create the {@link InjectionScope} for this spell with all {@link Resource resources} for later
   * injection.
   */
  private SpellScope createSpellScope() {
    SpellScope spellScope = new SpellScope(parentScope);
    spellScope.registerResource(Injector.class, new Injector() {
      @Override
      public <T> T injectMembers(T instance) throws IllegalStateException {
        return spellScope.injectMembers(instance);
      }

      @Override
      public <T> T getInstance(Class<T> cls) throws IllegalStateException {
        return spellScope.getInstance(cls);
      }
    });
    spellScope.registerResource(Config.class, new Config() {
      @Override
      public long getLuaTickLimit() {
        return SpellProgram.this.getLuaTicksLimit();
      }

      @Override
      public long getEventInterceptorTickLimit() {
        return config.getGeneralConfig().getEventListenerLuaTicksLimit();
      }

      @Override
      public ScriptGatewayConfig getScriptGatewayConfig() {
        return new ScriptGatewayConfig() {
          @Override
          public boolean isEnabled() {
            return config.getScriptGatewayConfig().isEnabled();
          }

          @Override
          public Path getScriptDir() {
            return config.getScriptGatewayConfig().getDir();
          }

          @Override
          public long getScriptTimeoutMillis() {
            return config.getScriptGatewayConfig().getTimeoutMillis();
          }
        };
      }
    });
    spellScope.registerResource(LuaConverters.class, spellScope.getInstance(Converters.class));
    spellScope.registerResource(LuaTypes.class, spellScope.getInstance(Types.class));
    spellScope.registerResource(Table.class, env);
    spellScope.registerResource(ExceptionHandler.class,
        (contextMessage, t) -> handleException(contextMessage, t));
    spellScope.registerResource(net.wizardsoflua.extension.spell.api.resource.LuaScheduler.class,
        scheduler);
    spellScope.registerResource(Spell.class,
        parallelTaskFactory -> parallelTaskFactories.add(parallelTaskFactory));
    spellScope.registerResource(TableFactory.class, stateContext);
    spellScope.registerResource(Time.class, new Time() {
      @Override
      public long getGameTime() {
        return world.getGameTime();
      }

      @Override
      public Clock getClock() {
        return time.getClock();
      }
    });
    return spellScope;
  }

  private void installSystemLibraries() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    FileSystem fileSystem = runtimeEnv.fileSystem();

    BasicLib.installInto(stateContext, env, /* runtimeEnv */ null, loader);

    // We don't pass the loader to the ModuleLib in order to prevent the installation of the
    // ChunkLoadPathSearcher
    ModuleLib.installInto(stateContext, env, /* runtimeEnv */ null, /* loader */ null,
        /* classLoader */ null);
    // Instead we install our own two searchers
    ClasspathResourceSearcher.installInto(env, loader, luaFunctionCache, classLoader);
    PatchedChunkLoadPathSearcher.installInto(env, loader, luaFunctionCache, classLoader, fileSystem,
        () -> luaPath);

    CoroutineLib.installInto(stateContext, env);
    StringLib.installInto(stateContext, env);
    MathLib.installInto(stateContext, env);
    TableLib.installInto(stateContext, env);

    WolRuntimeEnvironment runtimeEnvironment =
        new WolRuntimeEnvironment(RuntimeEnvironments.system(), this.fileSystem);
    IoLib.installInto(stateContext, env, runtimeEnvironment);
  }

  private void loadExtensions() {
    ExtensionLoader.getLuaToJavaConverters().forEach(this::registerLuaToJavaConverter);
    ExtensionLoader.getJavaToLuaConverters().forEach(this::registerJavaToLuaConverter);
    ExtensionLoader.getSpellExtensions().forEach(spellScope::getInstance);
  }

  private <C extends LuaToJavaConverter<?, ?>> void registerLuaToJavaConverter(
      Class<C> converterClass) {
    C converter = spellScope.getInstance(converterClass);
    getConverters().registerLuaToJavaConverter(converter);
  }

  private <C extends JavaToLuaConverter<?>> void registerJavaToLuaConverter(
      Class<C> converterClass) {
    C converter = spellScope.getInstance(converterClass);
    getConverters().registerJavaToLuaConverter(converter);
  }

  private int getLuaTicksLimit() {
    return config.getGeneralConfig().getLuaTicksLimit();
  }

  public Converters getConverters() {
    return spellScope.getInstance(Converters.class);
  }

  public EventsModule getEvents() {
    return spellScope.getInstance(EventsModule.class);
  }

  public ViewFactory getViewFactory() {
    return spellScope.getInstance(ViewFactory.class);
  }

  public void setSpellEntity(SpellEntity spellEntity) {
    this.spellEntity = checkNotNull(spellEntity, "spellEntity==null!");
    spellScope.registerResource(SpellEntity.class, spellEntity);
    loadExtensions();
  }

  public String getCode() {
    return code;
  }

  public @Nullable Entity getOwner() {
    return owner;
  }

  public void replacePlayer(@Nullable EntityPlayerMP oldPlayer,
      @Nullable EntityPlayerMP newPlayer) {
    if (owner == oldPlayer) {
      owner = newPlayer;
    }
  }

  public boolean isTerminated() {
    if (state == State.TERMINATED) {
      return true;
    }
    if (state == State.FINISHED) {
      for (ParallelTaskFactory parallelTaskFactory : parallelTaskFactories) {
        if (!parallelTaskFactory.isFinished()) {
          return false;
        }
      }
      terminate();
      return true;
    }
    return false;
  }

  public void terminate() {
    state = State.TERMINATED;
    for (ParallelTaskFactory parallelTaskFactory : parallelTaskFactories) {
      parallelTaskFactory.terminate();
    }
    spellScope.close();
  }

  public void resume() {
    try {
      switch (state) {
        case NEW:
          compileAndRun();
          break;
        case SLEEPING:
          if (wakeUpTime > world.getGameTime()) {
            return;
          }
        case PAUSED:
          scheduler.resume(getLuaTicksLimit(), continuation);
          break;
        case FINISHED:
        case TERMINATED:
          return;
      }
      state = State.FINISHED;
    } catch (CallFellAsleepException ex) {
      int sleepDuration = ex.getSleepDuration();
      wakeUpTime = world.getGameTime() + sleepDuration;
      continuation = ex.getContinuation();
      state = State.SLEEPING;
    } catch (CallPausedException ex) {
      continuation = ex.getContinuation();
      state = State.PAUSED;
    } catch (Exception ex) {
      handleException("Error during spell execution", ex);
    }
  }

  private void compileAndRun()
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    SpellModule.installInto(env, getConverters(), spellEntity);
    SpellsModule.installInto(env, getConverters(), spellRegistry, spellEntity);

    int luaTicksLimit = getLuaTicksLimit();
    dependencies.installModules(env, scheduler, luaTicksLimit);

    LuaFunction commandLineFunc = loader.loadTextChunk(new Variable(env), "command-line", code);
    scheduler.call(luaTicksLimit, commandLineFunc, arguments.toArray());
  }

  private void handleException(String contextMessage, Throwable t) {
    terminate();
    SpellException s = exceptionFactory.create(t);
    String message = String.format("%s: %s", contextMessage, s.getMessage());
    LOGGER.error(message, s);
    if (owner != null) {
      TextComponentString txt = new TextComponentString(message);
      txt.setStyle(new Style().setColor(TextFormatting.RED).setBold(Boolean.valueOf(true)));
      owner.sendMessage(txt);
    }
  }
}
