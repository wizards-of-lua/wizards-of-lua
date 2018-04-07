package net.wizardsoflua.lua.extension.api;

import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.Continuation;
import net.sandius.rembulan.runtime.LuaFunction;

public interface LuaExecutor {
  Object[] call(long luaTickLimit, LuaFunction function, Object... args)
      throws CallException, CallPausedException, InterruptedException;

  Object[] callUnpausable(long luaTickLimit, LuaFunction function, Object... args)
      throws CallException, InterruptedException;

  Object[] resume(long luaTickLimit, Continuation continuation)
      throws CallException, CallPausedException, InterruptedException;

  Object[] resumeUnpausable(long luaTickLimit, Continuation continuation)
      throws CallException, InterruptedException;
}
