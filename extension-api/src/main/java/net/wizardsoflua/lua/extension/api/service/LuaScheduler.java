package net.wizardsoflua.lua.extension.api.service;

import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.Continuation;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.lua.extension.api.PauseContext;

public interface LuaScheduler {
  void addPauseContext(PauseContext context);

  Object[] call(long luaTickLimit, LuaFunction function, Object... args)
      throws CallException, CallPausedException, InterruptedException;

  Object[] callUnpausable(long luaTickLimit, LuaFunction function, Object... args)
      throws CallException, InterruptedException;

  Object[] resume(long luaTickLimit, Continuation continuation)
      throws CallException, CallPausedException, InterruptedException;

  Object[] resumeUnpausable(long luaTickLimit, Continuation continuation)
      throws CallException, InterruptedException;

  void pause(ExecutionContext context) throws UnresolvedControlThrowable;

  void pauseIfRequested(ExecutionContext context) throws UnresolvedControlThrowable;

  void sleep(ExecutionContext context, int ticks) throws UnresolvedControlThrowable;

  long getAllowance();

  boolean isAutosleep();

  void setAutosleep(boolean autosleep);

  long getTotalLuaTicks();
}
