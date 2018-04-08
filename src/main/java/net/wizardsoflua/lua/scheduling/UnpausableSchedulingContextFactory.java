package net.wizardsoflua.lua.scheduling;

import static com.google.common.base.Preconditions.checkNotNull;

import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.lua.scheduling.LuaSchedulingContext.Context;

public class UnpausableSchedulingContextFactory implements LuaSchedulingContextFactory {
  private final Context context;
  private long luaTickLimit;

  public UnpausableSchedulingContextFactory(long luaTickLimit, Context context) {
    this.context = checkNotNull(context, "context == null!");
    this.luaTickLimit = luaTickLimit;
  }

  @Override
  public LuaSchedulingContext newInstance() {
    return new LuaSchedulingContext(luaTickLimit, context) {
      @Override
      public boolean isAutosleep() {
        return false;
      }

      @Override
      public void setAutosleep(boolean autosleep) {
        throw new IllegalOperationAttemptException("attempt to set autosleep");
      }

      @Override
      public void pause(ExecutionContext context) throws UnresolvedControlThrowable {
        throw new IllegalOperationAttemptException("attempt to sleep");
      }

      @Override
      public void pauseIfRequested(ExecutionContext context) {
        throw new IllegalOperationAttemptException("attempt to sleep");
      }
    };
  }
}
