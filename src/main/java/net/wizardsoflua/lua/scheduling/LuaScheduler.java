package net.wizardsoflua.lua.scheduling;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static net.sandius.rembulan.exec.DirectCallExecutor.newExecutor;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.Continuation;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.extension.spell.api.LuaTickListener;
import net.wizardsoflua.extension.spell.api.PauseContext;

public class LuaScheduler implements net.wizardsoflua.extension.spell.api.resource.LuaScheduler {
  private final Set<LuaTickListener> tickListeners = new HashSet<>();
  private final Set<PauseContext> pauseContexts = new HashSet<>();
  private final StateContext stateContext;
  private final PausableSchedulingContextFactory.Context context;
  private @Nullable LuaSchedulingContext currentSchedulingContext;
  private boolean autosleep = true;

  public LuaScheduler(StateContext stateContext) {
    this.stateContext = checkNotNull(stateContext, "stateContext == null!");
    context = new PausableSchedulingContextFactory.Context() {
      @Override
      public void registerTicks(int ticks) {
        LuaScheduler.this.registerTicks(ticks);
      }

      @Override
      public boolean shouldPause() {
        return LuaScheduler.this.shouldPause();
      }

      @Override
      public void setAutosleep(boolean autosleep) {
        LuaScheduler.this.autosleep = autosleep;
      }

      @Override
      public boolean isAutosleep() {
        return autosleep;
      }
    };
  }

  private void registerTicks(int ticks) {
    for (LuaTickListener tickListener : tickListeners) {
      tickListener.registerTicks(ticks);
    }
  }

  private boolean shouldPause() {
    for (PauseContext context : pauseContexts) {
      if (context.shouldPause()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean addTickListener(LuaTickListener tickListener) {
    return tickListeners.add(tickListener);
  }

  @Override
  public boolean removeTickListener(LuaTickListener tickListener) {
    return tickListeners.remove(tickListener);
  }

  @Override
  public boolean addPauseContext(PauseContext pauseContext) {
    return pauseContexts.add(pauseContext);
  }

  @Override
  public boolean removePauseContext(PauseContext pauseContext) {
    return pauseContexts.remove(pauseContext);
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
    DirectCallExecutor executor = newExecutor(
        wrap(new UnpausableSchedulingContextFactory(luaTickLimit, this::registerTicks)));
    try {
      return call(executor, callable);
    } catch (CallPausedException ex) {
      // This shouldn't happen unless ExecutionContext.pause is used instead of LuaScheduler.pause
      throw new UndeclaredThrowableException(ex);
    }
  }

  private Object[] call(DirectCallExecutor executor, LuaCallable callable)
      throws CallException, CallPausedException, InterruptedException, CallFellAsleepException {
    LuaSchedulingContext previousSchedulingContext = currentSchedulingContext;
    try {
      return callable.call(executor);
    } catch (CallPausedException ex) {
      int sleepDuration = currentSchedulingContext.getSleepDuration();
      if (sleepDuration > 0) {
        throw new CallFellAsleepException(sleepDuration, ex.getContinuation());
      } else {
        throw ex;
      }
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

  private LuaSchedulingContext getCurrentSchedulingContextNonNull() {
    checkState(currentSchedulingContext != null, "This method can only be called through Lua");
    return currentSchedulingContext;
  }

  @Override
  public void pause(ExecutionContext context)
      throws UnresolvedControlThrowable, IllegalOperationAttemptException {
    getCurrentSchedulingContextNonNull().pause(context);
  }

  @Override
  public void pauseIfRequested(ExecutionContext context)
      throws UnresolvedControlThrowable, IllegalOperationAttemptException {
    getCurrentSchedulingContextNonNull().pauseIfRequested(context);
  }

  @Override
  public void sleep(ExecutionContext context, int ticks) throws UnresolvedControlThrowable {
    if (ticks > 0) {
      LuaSchedulingContext schedulingContext = getCurrentSchedulingContextNonNull();
      schedulingContext.setSleepDuration(ticks);
      schedulingContext.pause(context);
    }
  }

  @Override
  public long getAllowance() {
    return getCurrentSchedulingContextNonNull().getAllowance();
  }

  @Override
  public boolean isAutosleep() {
    return getCurrentSchedulingContextNonNull().isAutosleep();
  }

  @Override
  public void setAutosleep(boolean autosleep) {
    getCurrentSchedulingContextNonNull().setAutosleep(autosleep);
  }
}
