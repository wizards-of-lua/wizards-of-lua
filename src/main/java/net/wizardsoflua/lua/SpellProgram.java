package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.FileSystem;

import net.minecraft.command.ICommandSender;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.Continuation;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.impl.StateContexts;
import net.sandius.rembulan.lib.BasicLib;
import net.sandius.rembulan.lib.CoroutineLib;
import net.sandius.rembulan.lib.MathLib;
import net.sandius.rembulan.lib.ModuleLib;
import net.sandius.rembulan.lib.StringLib;
import net.sandius.rembulan.lib.TableLib;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.SchedulingContext;
import net.sandius.rembulan.runtime.SchedulingContextFactory;
import net.wizardsoflua.lua.classes.LuaClasses;
import net.wizardsoflua.lua.compiler.PatchedCompilerChunkLoader;
import net.wizardsoflua.lua.dependency.ModuleDependencies;
import net.wizardsoflua.lua.module.blocks.BlocksModule;
import net.wizardsoflua.lua.module.entities.EntitiesModule;
import net.wizardsoflua.lua.module.events.EventHandlers;
import net.wizardsoflua.lua.module.events.EventsModule;
import net.wizardsoflua.lua.module.luapath.AddPathFunction;
import net.wizardsoflua.lua.module.print.PrintRedirector;
import net.wizardsoflua.lua.module.searcher.ClasspathResourceSearcher;
import net.wizardsoflua.lua.module.searcher.LuaFunctionBinaryCache;
import net.wizardsoflua.lua.module.searcher.PatchedChunkLoadPathSearcher;
import net.wizardsoflua.lua.module.spell.SpellModule;
import net.wizardsoflua.lua.module.time.Time;
import net.wizardsoflua.lua.module.time.TimeModule;
import net.wizardsoflua.lua.module.types.Types;
import net.wizardsoflua.lua.module.types.TypesModule;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.spell.SpellException;
import net.wizardsoflua.spell.SpellExceptionFactory;

public class SpellProgram {
  private enum State {
    NEW, PAUSED, FINISHED
  }
  public interface Context {

    String getLuaPathElementOfPlayer(String nameOrUuid);

    LuaClasses getLuaClasses();

    LuaFunctionBinaryCache getLuaFunctionBinaryCache();

  }

  private static final String ROOT_CLASS_PREFIX = "SpellByteCode";
  private final String code;
  private final ModuleDependencies dependencies;
  private final DirectCallExecutor executor;
  private final StateContext stateContext;
  private final Table env;
  private final PatchedCompilerChunkLoader loader;
  private final SpellRuntimeEnvironment runtimeEnv;
  private final SpellExceptionFactory exceptionFactory;
  private final Types types;
  private final Converters converters;
  private final EventHandlers eventHandlers;
  private State state;

  private Continuation continuation;
  private SpellEntity spellEntity;
  private Time time;
  private final Context context;

  SpellProgram(String code, ModuleDependencies dependencies, ICommandSender owner,
      String defaultLuaPath, Time time, Context context) {
    this.code = checkNotNull(code, "code==null!");
    this.dependencies = checkNotNull(dependencies, "dependencies==null!");
    checkNotNull(owner, "source==null!");;
    this.time = checkNotNull(time, "time==null!");
    this.context = checkNotNull(context, "context==null!");

    stateContext = StateContexts.newDefaultInstance();
    env = stateContext.newTable();
    types = new Types(env);
    this.executor = DirectCallExecutor.newExecutor(createSchedulingContextFactory());
    runtimeEnv = new SpellRuntimeEnvironment(new SpellRuntimeEnvironment.Context() {
      @Override
      public String getLuaPath() {
        return defaultLuaPath;
      }
    });
    loader = PatchedCompilerChunkLoader.of(ROOT_CLASS_PREFIX);
    exceptionFactory = new SpellExceptionFactory(ROOT_CLASS_PREFIX);
    installSystemLibraries();
    converters = new Converters(types, context.getLuaClasses());
    TypesModule.installInto(env, types, converters);
    PrintRedirector.installInto(env, owner);
    AddPathFunction.installInto(env, converters, new AddPathFunction.Context() {
      @Override
      public String getLuaPathElementOfPlayer(String nameOrUuid) {
        return context.getLuaPathElementOfPlayer(nameOrUuid);
      }
    });
    TimeModule.installInto(env, converters, time);
    BlocksModule.installInto(env, converters);
    eventHandlers = new EventHandlers(converters, createEventHandlersContext());
    EventsModule.installInto(env, converters, eventHandlers);

    state = State.NEW;
  }

  private EventHandlers.Context createEventHandlersContext() {
    return new EventHandlers.Context() {

      @Override
      public long getCurrentTime() {
        return time.getGameTotalTime();
      }
    };
  }

  private SchedulingContextFactory createSchedulingContextFactory() {
    return new SchedulingContextFactory() {
      @Override
      public SchedulingContext newInstance() {
        time.resetAllowance();
        return new SchedulingContext() {

          @Override
          public boolean shouldPause() {
            boolean result = time.shouldPause() || eventHandlers.shouldPause();
            return result;
          }

          @Override
          public void registerTicks(int ticks) {
            time.consumeLuaTicks(ticks);
          }
        };
      }
    };
  }


  public void setSpellEntity(SpellEntity spellEntity) {
    this.spellEntity = spellEntity;
  }

  public String getCode() {
    return code;
  }

  public EventHandlers getEventHandlers() {
    return eventHandlers;
  }

  public boolean isTerminated() {
    return state == State.FINISHED;
  }

  public void terminate() {
    state = State.FINISHED;
  }

  public void resume() throws SpellException {
    try {
      switch (state) {
        case NEW:
          try {
            compileAndRun();
            state = State.FINISHED;
          } catch (CallPausedException e) {
            continuation = e.getContinuation();
            state = State.PAUSED;
          } catch (Exception e) {
            state = State.FINISHED;
            throw e;
          }
          break;
        case PAUSED:
          try {
            executor.resume(continuation);
            state = State.FINISHED;
          } catch (CallPausedException e) {
            continuation = e.getContinuation();
            state = State.PAUSED;
          } catch (Exception e) {
            state = State.FINISHED;
            throw e;
          }
          break;
        default:
          break;
      }
    } catch (Exception ex) {
      throw exceptionFactory.create(ex);
    }
  }

  private void compileAndRun()
      throws LoaderException, CallException, CallPausedException, InterruptedException {

    SpellModule.installInto(env, converters, spellEntity);
    EntitiesModule.installInto(env, converters, spellEntity);

    dependencies.installModules(env, executor, stateContext);

    LuaFunction commandLineFunc = loader.loadTextChunk(new Variable(env), "command-line", code);
    executor.call(stateContext, commandLineFunc);
  }

  private void installSystemLibraries() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    FileSystem fileSystem = runtimeEnv.fileSystem();
    LuaFunctionBinaryCache luaFunctionCache = context.getLuaFunctionBinaryCache();

    BasicLib.installInto(stateContext, env, runtimeEnv, loader);

    // We don't pass the loader to the ModuleLib in order to prevent the installation of the
    // ChunkLoadPathSearcher
    ModuleLib.installInto(stateContext, env, runtimeEnv, /* loader */ null, classLoader);
    // Instead we install our own two searchers
    ClasspathResourceSearcher.installInto(env, loader, luaFunctionCache, classLoader);
    PatchedChunkLoadPathSearcher.installInto(env, loader, luaFunctionCache, classLoader,
        fileSystem);

    CoroutineLib.installInto(stateContext, env);
    StringLib.installInto(stateContext, env);
    MathLib.installInto(stateContext, env);
    TableLib.installInto(stateContext, env);

  }

}
