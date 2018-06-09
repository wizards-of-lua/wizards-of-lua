package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;

import com.google.common.cache.Cache;

import net.minecraft.command.ICommandSender;
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
import net.sandius.rembulan.lib.MathLib;
import net.sandius.rembulan.lib.ModuleLib;
import net.sandius.rembulan.lib.StringLib;
import net.sandius.rembulan.lib.TableLib;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;
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
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.extension.spell.spi.LuaToJavaConverter;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.entity.PlayerApi;
import net.wizardsoflua.lua.classes.entity.PlayerClass;
import net.wizardsoflua.lua.classes.entity.PlayerInstance;
import net.wizardsoflua.lua.compiler.PatchedCompilerChunkLoader;
import net.wizardsoflua.lua.dependency.ModuleDependencies;
import net.wizardsoflua.lua.extension.InjectionScope;
import net.wizardsoflua.lua.extension.ServiceLoader;
import net.wizardsoflua.lua.extension.SpellScope;
import net.wizardsoflua.lua.module.entities.EntitiesModule;
import net.wizardsoflua.lua.module.events.EventsModule;
import net.wizardsoflua.lua.module.luapath.AddPathFunction;
import net.wizardsoflua.lua.module.print.PrintRedirector;
import net.wizardsoflua.lua.module.searcher.ClasspathResourceSearcher;
import net.wizardsoflua.lua.module.searcher.LuaFunctionBinaryCache;
import net.wizardsoflua.lua.module.searcher.PatchedChunkLoadPathSearcher;
import net.wizardsoflua.lua.module.spell.SpellModule;
import net.wizardsoflua.lua.module.spell.SpellsModule;
import net.wizardsoflua.lua.scheduling.CallFellAsleepException;
import net.wizardsoflua.lua.scheduling.LuaScheduler;
import net.wizardsoflua.lua.scheduling.LuaSchedulingContext;
import net.wizardsoflua.lua.view.ViewFactory;
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

    int getLuaTicksLimit();

    int getEventListenerLuaTicksLimit();

    boolean isScriptGatewayEnabled();

    Path getScriptDir();

    long getScriptTimeoutMillis();

    SpellRegistry getSpellRegistry();

    InjectionScope getRootScope();
  }

  public static final String ROOT_CLASS_PREFIX = "SpellByteCode";
  private final String code;
  private final ModuleDependencies dependencies;
  private final LuaScheduler scheduler;
  private final StateContext stateContext;
  private final Table env;
  private final PatchedCompilerChunkLoader loader;
  private final RuntimeEnvironment runtimeEnv;
  private final SpellExceptionFactory exceptionFactory;
  private final LuaClassLoader luaClassLoader;
  private final InjectionScope injectionScope;
  private final Collection<ParallelTaskFactory> parallelTaskFactories = new ArrayList<>();
  private final long luaTickLimit;
  private ICommandSender owner;
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

  SpellProgram(ICommandSender owner, String code, ModuleDependencies dependencies,
      String defaultLuaPath, World world, Context context, Logger logger) {
    this.owner = checkNotNull(owner, "owner==null!");
    this.code = checkNotNull(code, "code==null!");
    this.dependencies = checkNotNull(dependencies, "dependencies==null!");
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
    luaClassLoader = new LuaClassLoader(env, new LuaClassLoader.Context() {
      @Override
      public @Nullable LuaSchedulingContext getCurrentSchedulingContext() {
        return scheduler.getCurrentSchedulingContext();
      }
    });
    injectionScope = createInjectionScope();
    loadServices(logger);
    injectionScope.injectMembers(luaClassLoader);
    luaClassLoader.loadStandardClasses();
    PrintRedirector.installInto(env, new PrintRedirector.Context() {
      @Override
      public void send(String message) {
        SpellProgram.this.owner.sendMessage(new TextComponentString(message));
      }
    });
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
    scope.registerResource(LuaConverters.class, luaClassLoader.getConverters());
    scope.registerResource(LuaTypes.class, luaClassLoader.getTypes());
    scope.registerResource(Table.class, env);
    scope.registerResource(ExceptionHandler.class, new ExceptionHandler() {
      @Override
      public void handle(String contextMessage, Throwable t) {
        handleException(contextMessage, t);
      }
    });
    scope.registerResource(net.wizardsoflua.extension.spell.api.resource.LuaScheduler.class,
        scheduler);
    scope.registerResource(Spell.class, new Spell() {
      @Override
      public void addParallelTaskFactory(ParallelTaskFactory parallelTaskFactory) {
        parallelTaskFactories.add(parallelTaskFactory);
      }
    });
    scope.registerResource(TableFactory.class, stateContext);
    scope.registerResource(Time.class, new Time() {
      @Override
      public long getTotalWorldTime() {
        return world.getTotalWorldTime();
      }

      @Override
      public Clock getClock() {
        return context.getClock();
      }
    });
    return scope;
  }

  private void loadServices(Logger logger) {
    Set<Class<? extends LuaConverter<?, ?>>> converters =
        ServiceLoader.load(logger, LuaConverter.getClassWithWildcards());

    Set<Class<? extends LuaToJavaConverter<?, ?>>> luaToJava = new HashSet<>(converters);
    luaToJava.addAll(ServiceLoader.load(logger, LuaToJavaConverter.getClassWithWildcards()));
    luaToJava.forEach(this::registerLuaToJavaConverter);

    Set<Class<? extends JavaToLuaConverter<?>>> javaToLua = new HashSet<>(converters);
    javaToLua.addAll(ServiceLoader.load(logger, JavaToLuaConverter.getClassWithWildcards()));
    javaToLua.forEach(this::registerJavaToLuaConverter);

    ServiceLoader.load(logger, SpellExtension.class).forEach(injectionScope::getInstance);
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

  public LuaClassLoader getLuaClassLoader() {
    return luaClassLoader;
  }

  private Converters getConverters() {
    return luaClassLoader.getConverters();
  }

  public EventsModule getEvents() {
    return injectionScope.getInstance(EventsModule.class);
  }

  public ViewFactory getViewFactory() {
    return injectionScope.getInstance(ViewFactory.class);
  }

  public void setSpellEntity(SpellEntity spellEntity) {
    this.spellEntity = spellEntity;
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
          if (wakeUpTime > world.getTotalWorldTime()) {
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
      wakeUpTime = world.getTotalWorldTime() + sleepDuration;
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
    txt.setStyle((new Style()).setColor(TextFormatting.RED).setBold(Boolean.valueOf(true)));
    owner.sendMessage(txt);
  }

  private void compileAndRun()
      throws LoaderException, CallException, CallPausedException, InterruptedException {

    SpellModule.installInto(env, getConverters(), spellEntity);
    EntitiesModule.installInto(env, getConverters(), spellEntity);
    SpellsModule.installInto(env, getConverters(), context.getSpellRegistry(), spellEntity);

    dependencies.installModules(env, scheduler, luaTickLimit);

    LuaFunction commandLineFunc = loader.loadTextChunk(new Variable(env), "command-line", code);
    scheduler.call(luaTickLimit, commandLineFunc);
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
        new PatchedChunkLoadPathSearcher.Context() {
          @Override
          public String getLuaPath() {
            return defaultLuaPath;
          }
        });

    CoroutineLib.installInto(stateContext, env);
    StringLib.installInto(stateContext, env);
    MathLib.installInto(stateContext, env);
    TableLib.installInto(stateContext, env);
  }

  public void replacePlayerInstance(EntityPlayerMP newPlayer) {
    if (owner.getCommandSenderEntity() instanceof EntityPlayer) {
      if (owner.getCommandSenderEntity().getUniqueID().equals(newPlayer.getUniqueID())) {
        owner = newPlayer;
      }
    }
    PlayerClass playerClass = luaClassLoader.getLuaClassOfType(PlayerClass.class);
    Cache<EntityPlayerMP, PlayerInstance<PlayerApi<EntityPlayerMP>, EntityPlayerMP>> cache =
        playerClass.getCache();
    for (EntityPlayer oldPlayer : cache.asMap().keySet()) {
      if (oldPlayer.getUniqueID().equals(newPlayer.getUniqueID())) {
        PlayerInstance<PlayerApi<EntityPlayerMP>, EntityPlayerMP> oldValue =
            cache.asMap().remove(oldPlayer);
        cache.put(newPlayer, oldValue);
        oldValue.setDelegate(newPlayer);
      }
    }
  }
}
