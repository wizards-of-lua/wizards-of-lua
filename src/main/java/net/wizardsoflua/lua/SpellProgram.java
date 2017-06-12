package net.wizardsoflua.lua;

import net.minecraft.command.ICommandSender;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.impl.StateContexts;
import net.sandius.rembulan.lib.BasicLib;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.SchedulingContextFactory;
import net.wizardsoflua.lua.patched.PatchedCompilerChunkLoader;
import net.wizardsoflua.spell.SpellException;
import net.wizardsoflua.spell.SpellExceptionFactory;

public class SpellProgram {
  private static final String ROOT_CLASS_PREFIX = "LuaProgramAsJavaByteCode";
  private final String code;
  private final DirectCallExecutor executor;
  private final StateContext stateContext;
  private final Table env;
  private final PatchedCompilerChunkLoader loader;
  private final SpellRuntimeEnvironment runtimeEnv;
  private final SpellExceptionFactory exceptionFactory;

  private enum State {
    NEW, FINISHED
  }

  private State state;

  public SpellProgram(ICommandSender source, String code,
      DirectCallExecutor executor) {
    this.code = code;
    this.executor = executor; 
    stateContext = StateContexts.newDefaultInstance();
    env = stateContext.newTable();
    runtimeEnv = new SpellRuntimeEnvironment();
    loader = PatchedCompilerChunkLoader.of(ROOT_CLASS_PREFIX);
    exceptionFactory = new SpellExceptionFactory(ROOT_CLASS_PREFIX);

    BasicLib.installInto(stateContext, env, runtimeEnv, loader);
    PrintRedirector.installInto(env, source);
    state = State.NEW;
  }

  public void resume() throws SpellException {
    try {
      switch (state) {
        case NEW:
          try {
            compileAndRun();
          } finally {
            state = State.FINISHED;
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
