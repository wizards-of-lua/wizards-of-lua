package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;
import com.google.common.cache.Cache;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.sandius.rembulan.Conversions;
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
import net.wizardsoflua.WizardsOfLua;
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
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.classes.entity.PlayerClass;
import net.wizardsoflua.lua.classes.entity.PlayerClass.Instance;
import net.wizardsoflua.lua.compiler.PatchedCompilerChunkLoader;
import net.wizardsoflua.lua.extension.InjectionScope;
import net.wizardsoflua.lua.extension.SpellScope;
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
  private enum State {
    NEW, SLEEPING, PAUSED, FINISHED, TERMINATED;
  }
  public interface Context {
    String getLuaPathElementOfPlayer(String nameOrUuid);

    LuaFunctionBinaryCache getLuaFunctionBinaryCache();

    Clock getClock();

    long getLuaTicksLimit();

    long getEventListenerLuaTicksLimit();

    boolean isScriptGatewayEnabled();

    Path getScriptDir();

    long getScriptTimeoutMillis();

    SpellRegistry getSpellRegistry();

    InjectionScope getRootScope();

    FileSystem getWorldFileSystem();

    Profiles getProfiles();
  }

  public static final String ROOT_CLASS_PREFIX = "SpellByteCode";
  private final String code;
  private final LuaScheduler scheduler;
  private final StateContext stateContext;
  private final Table env;
  private final PatchedCompilerChunkLoader loader;
  private final RuntimeEnvironment runtimeEnv;
  private final SpellExceptionFactory exceptionFactory;
  private final InjectionScope injectionScope;
  private final Collection<ParallelTaskFactory> parallelTaskFactories = new ArrayList<>();
  private final long luaTickLimit;
  private Entity owner;
  private State state = State.NEW;
  /**
   * The totalWorldTime at which this program should stop sleeping.
   */
  private long wakeUpTime;

  private Continuation continuation;
  private SpellEntity spellEntity;
  private String defaultLuaPath;
  private final World world;
  private final Context context;
  private final String[] arguments;

  SpellProgram(Entity owner, String code, @Nullable String[] arguments, String defaultLuaPath,
      World world, PrintReceiver printReceiver, Context context, Logger logger) {
    this.owner = checkNotNull(owner, "owner==null!");
    this.code = checkNotNull(code, "code==null!");
    this.arguments = arguments;
    this.defaultLuaPath = checkNotNull(defaultLuaPath, "defaultLuaPath==null!");
    this.world = checkNotNull(world, "world == null!");
    this.context = checkNotNull(context, "context==null!");

    luaTickLimit = context.getLuaTicksLimit();
    stateContext = StateContexts.newDefaultInstance();
    scheduler = new LuaScheduler(stateContext);

    env = stateContext.newTable();
    runtimeEnv = RuntimeEnvironments.system();
    loader = PatchedCompilerChunkLoader.of(ROOT_CLASS_PREFIX);
    exceptionFactory = new SpellExceptionFactory();
    installSystemLibraries();
    injectionScope = createInjectionScope();
    PrintRedirector.installInto(env, printReceiver);
    AddPathFunction.installInto(env, getConverters(), new AddPathFunction.Context() {
      @Override
      public String getLuaPathElementOfPlayer(String nameOrUuid) {
        return context.getLuaPathElementOfPlayer(nameOrUuid);
      }

      @Override
      public void addPath(String pathelement) {
        SpellProgram.this.defaultLuaPath += ";" + pathelement;
      }
    });
  }

  /**
   * Create the {@link InjectionScope} for this spell with all {@link Resource resources} for later
   * injection.
   */
  private InjectionScope createInjectionScope() {
    InjectionScope rootScope = context.getRootScope();
    InjectionScope scope = new SpellScope(rootScope);
    scope.registerResource(Injector.class, new Injector() {
      @Override
      public <T> T injectMembers(T instance) throws IllegalStateException {
        return scope.injectMembers(instance);
      }

      @Override
      public <T> T getInstance(Class<T> cls) throws IllegalStateException {
        return scope.getInstance(cls);
      }
    });
    scope.registerResource(Config.class, new Config() {
      @Override
      public long getLuaTickLimit() {
        return luaTickLimit;
      }

      @Override
      public long getEventInterceptorTickLimit() {
        return context.getEventListenerLuaTicksLimit();
      }

      @Override
      public ScriptGatewayConfig getScriptGatewayConfig() {
        return new ScriptGatewayConfig() {
          @Override
          public boolean isEnabled() {
            return context.isScriptGatewayEnabled();
          }

          @Override
          public Path getScriptDir() {
            return context.getScriptDir();
          }

          @Override
          public long getScriptTimeoutMillis() {
            return context.getScriptTimeoutMillis();
          }
        };
      }
    });
    scope.registerResource(LuaConverters.class, scope.getInstance(Converters.class));
    scope.registerResource(LuaTypes.class, scope.getInstance(Types.class));
    scope.registerResource(Table.class, env);
    scope.registerResource(ExceptionHandler.class,
        (contextMessage, t) -> handleException(contextMessage, t));
    scope.registerResource(net.wizardsoflua.extension.spell.api.resource.LuaScheduler.class,
        scheduler);
    scope.registerResource(Spell.class,
        parallelTaskFactory -> parallelTaskFactories.add(parallelTaskFactory));
    scope.registerResource(TableFactory.class, stateContext);
    scope.registerResource(Time.class, new Time() {
      @Override
      public long getGameTime() {
        return world.getGameTime();
      }

      @Override
      public Clock getClock() {
        return context.getClock();
      }

    });
    scope.registerResource(MinecraftServer.class, world.getServer());
    scope.registerResource(WizardsOfLua.class, WizardsOfLua.instance);
    return scope;
  }

  private void loadExtensions() {
    ExtensionLoader.getLuaToJavaConverters().forEach(this::registerLuaToJavaConverter);
    ExtensionLoader.getJavaToLuaConverters().forEach(this::registerJavaToLuaConverter);
    ExtensionLoader.getSpellExtension().forEach(injectionScope::getInstance);
  }

  private <C extends LuaToJavaConverter<?, ?>> void registerLuaToJavaConverter(
      Class<C> converterClass) {
    C converter = injectionScope.getInstance(converterClass);
    getConverters().registerLuaToJavaConverter(converter);
  }

  private <C extends JavaToLuaConverter<?>> void registerJavaToLuaConverter(
      Class<C> converterClass) {
    C converter = injectionScope.getInstance(converterClass);
    getConverters().registerJavaToLuaConverter(converter);
  }

  public Converters getConverters() {
    return injectionScope.getInstance(Converters.class);
  }

  public EventsModule getEvents() {
    return injectionScope.getInstance(EventsModule.class);
  }

  public ViewFactory getViewFactory() {
    return injectionScope.getInstance(ViewFactory.class);
  }

  public void setSpellEntity(SpellEntity spellEntity) {
    this.spellEntity = checkNotNull(spellEntity, "spellEntity==null!");
    injectionScope.registerResource(SpellEntity.class, spellEntity);
    loadExtensions();
  }

  public String getCode() {
    return code;
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
          scheduler.resume(luaTickLimit, continuation);
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

  private void handleException(String contextMessage, Throwable t) {
    terminate();
    SpellException s = exceptionFactory.create(t);
    s.printStackTrace();
    String message = String.format("%s: %s", contextMessage, s.getMessage());
    TextComponentString txt = new TextComponentString(message);
    txt.setStyle(new Style().setColor(TextFormatting.RED).setBold(Boolean.valueOf(true)));
    owner.sendMessage(txt);
  }

  private void compileAndRun()
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    SpellModule.installInto(env, getConverters(), spellEntity);
    SpellsModule.installInto(env, getConverters(), context.getSpellRegistry(), spellEntity);

    LuaFunction requireFunction =
        checkNotNull((LuaFunction) env.rawget("require"), "Missing require function!");
    for (String module : Arrays.asList( //
        "wol.Globals", //
        "wol.inspect", //
        "wol.Check", //
        "wol.Object", //
        "wol.Vec3", //
        "wol.Material", //
        "wol.Block", //
        "wol.Entity", //
        "wol.Spell", //
        "wol.Player" //
    )) {
      scheduler.callUnpausable(Long.MAX_VALUE, requireFunction, module);
    }

    String code = getProfileRequireCalls() + this.code;
    LuaFunction commandLineFunc = loader.loadTextChunk(new Variable(env), "command-line", code);
    if (arguments != null) {
      Object[] luaArgs = new Object[arguments.length];
      System.arraycopy(arguments, 0, luaArgs, 0, luaArgs.length);
      Conversions.toCanonicalValues(luaArgs);
      scheduler.call(luaTickLimit, commandLineFunc, luaArgs);
    } else {
      scheduler.call(luaTickLimit, commandLineFunc);
    }
  }

  private CharSequence getProfileRequireCalls() {
    StringBuilder result = new StringBuilder();
    String sharedProfile = context.getProfiles().getSharedProfile();
    if (sharedProfile != null) {
      result.append("require('" + sharedProfile + "');");
    }

    if (owner instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) owner;
      String playerProfile = context.getProfiles().getProfile(player);
      if (playerProfile != null) {
        result.append("require('" + playerProfile + "');");
      }
    }
    return result;
  }

  private void installSystemLibraries() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    FileSystem fileSystem = runtimeEnv.fileSystem();
    LuaFunctionBinaryCache luaFunctionCache = context.getLuaFunctionBinaryCache();

    BasicLib.installInto(stateContext, env, /* runtimeEnv */ null, loader);

    // We don't pass the loader to the ModuleLib in order to prevent the installation of the
    // ChunkLoadPathSearcher
    ModuleLib.installInto(stateContext, env, /* runtimeEnv */ null, /* loader */ null,
        /* classLoader */ null);
    // Instead we install our own two searchers
    ClasspathResourceSearcher.installInto(env, loader, luaFunctionCache, classLoader);
    PatchedChunkLoadPathSearcher.installInto(env, loader, luaFunctionCache, classLoader, fileSystem,
        () -> defaultLuaPath);

    CoroutineLib.installInto(stateContext, env);
    StringLib.installInto(stateContext, env);
    MathLib.installInto(stateContext, env);
    TableLib.installInto(stateContext, env);

    FileSystem wolFs = context.getWorldFileSystem();
    WolRuntimeEnvironment runtimeEnvironment =
        new WolRuntimeEnvironment(RuntimeEnvironments.system(), wolFs);
    IoLib.installInto(stateContext, env, runtimeEnvironment);
  }

  public void replacePlayerInstance(EntityPlayerMP newPlayer) {
    if (owner instanceof EntityPlayer) {
      if (owner.getUniqueID().equals(newPlayer.getUniqueID())) {
        owner = newPlayer;
      }
    }
    PlayerClass playerClass = injectionScope.getInstance(PlayerClass.class);
    Cache<EntityPlayer, Delegator<? extends Instance<EntityPlayerMP>>> cache =
        playerClass.getCache();
    for (EntityPlayer oldPlayer : cache.asMap().keySet()) {
      if (oldPlayer.getUniqueID().equals(newPlayer.getUniqueID())) {
        Delegator<? extends Instance<EntityPlayerMP>> oldValue = cache.asMap().remove(oldPlayer);
        cache.put(newPlayer, oldValue);
        Instance<EntityPlayerMP> instance = oldValue.getDelegate();
        instance.setDelegate(newPlayer);
      }
    }
  }
}
