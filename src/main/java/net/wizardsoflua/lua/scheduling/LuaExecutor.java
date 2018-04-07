package net.wizardsoflua.lua.scheduling;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.sandius.rembulan.exec.DirectCallExecutor.newExecutor;

import java.lang.reflect.UndeclaredThrowableException;

import javax.annotation.Nullable;

import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.Continuation;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.SchedulingContext;

public class LuaExecutor implements net.wizardsoflua.lua.extension.api.LuaExecutor {
  private final StateContext stateContext;
  private final AggregatingSchedulingContext context;
  private @Nullable LuaSchedulingContext currentSchedulingContext;

  public LuaExecutor(StateContext stateContext) {
    this.stateContext = checkNotNull(stateContext, "stateContext == null!");
    context = new AggregatingSchedulingContext();
  }

  public void addSchedulingContext(SchedulingContext context) {
    this.context.addSchedulingContext(context);
  }

  @Override
  public Object[] call(long luaTickLimit, LuaFunction function, Object... args)
      throws CallException, CallPausedException, InterruptedException {
    return callPausable(luaTickLimit, executor -> executor.call(stateContext, function, args));
  }

  @Override
  public Object[] callUnpausable(long luaTickLimit, LuaFunction function, Object... args)
      throws CallException, InterruptedException {
    return callUnpausable(luaTickLimit, executor -> executor.call(stateContext, function, args));
  }

  @Override
  public Object[] resume(long luaTickLimit, Continuation continuation)
      throws CallException, CallPausedException, InterruptedException {
    return callPausable(luaTickLimit, executor -> executor.resume(continuation));
  }

  @Override
  public Object[] resumeUnpausable(long luaTickLimit, Continuation continuation)
      throws CallException, InterruptedException {
    return callUnpausable(luaTickLimit, executor -> executor.resume(continuation));
  }

  private Object[] callPausable(long luaTickLimit, LuaCallable callable)
      throws CallException, CallPausedException, InterruptedException {
    DirectCallExecutor executor =
        newExecutor(wrap(new PausableSchedulingContextFactory(luaTickLimit, context)));
    return call(executor, callable);
  }

  private Object[] callUnpausable(long luaTickLimit, LuaCallable callable)
      throws CallException, InterruptedException {
    DirectCallExecutor executor =
        newExecutor(wrap(new UnpausableSchedulingContextFactory(luaTickLimit)));
    try {
      return call(executor, callable);
    } catch (CallPausedException ex) {
      // This should never happen unless we have a programming error.
      throw new UndeclaredThrowableException(ex);
    }
  }

  private Object[] call(DirectCallExecutor executor, LuaCallable callable)
      throws CallException, CallPausedException, InterruptedException {
    LuaSchedulingContext previousSchedulingContext = currentSchedulingContext;
    try {
      return callable.call(executor);
    } finally {
      currentSchedulingContext = previousSchedulingContext;
    }
  }

  private LuaSchedulingContextFactory wrap(LuaSchedulingContextFactory delegate) {
    return new LuaSchedulingContextFactory() {
      @Override
      public LuaSchedulingContext newInstance() {
        currentSchedulingContext = delegate.newInstance();
        return currentSchedulingContext;
      }
    };
  }

  private interface LuaCallable {
    Object[] call(DirectCallExecutor executor)
        throws CallException, CallPausedException, InterruptedException;
  }

  public @Nullable LuaSchedulingContext getCurrentSchedulingContext() {
    return currentSchedulingContext;
  }
}
