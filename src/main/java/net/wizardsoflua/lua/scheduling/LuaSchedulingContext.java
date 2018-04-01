package net.wizardsoflua.lua.scheduling;

import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.SchedulingContext;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;

public abstract class LuaSchedulingContext implements SchedulingContext {
  private int allowance;

  public LuaSchedulingContext(int luaTickLimit) {
    this.allowance = luaTickLimit;
  }

  @Override
  public void registerTicks(int ticks) {
    allowance -= ticks;
  }

  @Override
  public boolean shouldPause() {
    if (allowance <= 0) {
      if (isAutosleep()) {
        return true;
      } else {
        throw new IllegalStateException(
            "Spell has been broken automatically since it has exceeded its tick allowance!");
      }
    } else {
      return false;
    }
  }

  public int getAllowance() {
    return allowance;
  }

  public abstract boolean isAutosleep();

  public abstract void setAutosleep(boolean autosleep);

  public abstract LuaExecutor.Type getLuaExecutorType();

  public abstract void pauseIfRequested(ExecutionContext context) throws UnresolvedControlThrowable;
}
