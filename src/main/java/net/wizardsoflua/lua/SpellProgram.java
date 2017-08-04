package net.wizardsoflua.lua;

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
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.SchedulingContextFactory;
import net.wizardsoflua.lua.patched.PatchedCompilerChunkLoader;
import net.wizardsoflua.lua.runtime.Runtime;
import net.wizardsoflua.lua.runtime.RuntimeModule;
import net.wizardsoflua.spell.SpellException;
import net.wizardsoflua.spell.SpellExceptionFactory;

public class SpellProgram {
  private enum State {
    NEW, PAUSED, FINISHED
  }
  public interface Context {

    SchedulingContextFactory getSchedulingContextFactory();

    Runtime getRuntime();

  }

  private static final String ROOT_CLASS_PREFIX = "SpellByteCode";
  private final ICommandSender source;
  private final String code;
  private final Context context;
  private final DirectCallExecutor executor;
  private final StateContext stateContext;
  private final Table env;
  private final PatchedCompilerChunkLoader loader;
  private final SpellRuntimeEnvironment runtimeEnv;
  private final SpellExceptionFactory exceptionFactory;

  private State state;
  private Continuation continuation;

  public SpellProgram(ICommandSender source, String code, Context context) {
    this.source = source;
    this.code = code;
    this.context = context;
    this.executor = DirectCallExecutor.newExecutor(context.getSchedulingContextFactory());
    stateContext = StateContexts.newDefaultInstance();
    env = stateContext.newTable();
    runtimeEnv = new SpellRuntimeEnvironment();
    loader = PatchedCompilerChunkLoader.of(ROOT_CLASS_PREFIX);
    exceptionFactory = new SpellExceptionFactory(ROOT_CLASS_PREFIX);

    installModules();
    
    state = State.NEW;
  }

  private void installModules() {
    BasicLib.installInto(stateContext, env, runtimeEnv, loader);
    PrintRedirector.installInto(env, source);
    RuntimeModule.installInto(env, context.getRuntime());
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
    LuaFunction commandLineFunc = loader.loadTextChunk(new Variable(env), "command-line", code);
    executor.call(stateContext, commandLineFunc);
  }

  public boolean isTerminated() {
    return state == State.FINISHED;
  }

}
