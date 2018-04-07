package net.wizardsoflua.lua.scheduling;

import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;

public class UnpausableSchedulingContextFactory implements LuaSchedulingContextFactory {
  private long luaTickLimit;

  public UnpausableSchedulingContextFactory(long luaTickLimit) {
    this.luaTickLimit = luaTickLimit;
  }

  @Override
  public LuaSchedulingContext newInstance() {
    return new LuaSchedulingContext(luaTickLimit) {
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
