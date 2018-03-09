package net.wizardsoflua.lua.scheduling;

import static com.google.common.base.Preconditions.checkNotNull;

import net.sandius.rembulan.runtime.SchedulingContext;

public abstract class LuaSchedulingContext implements SchedulingContext {
  private int allowance;
  private final SchedulingContext context;

  public LuaSchedulingContext(int luaTickLimit, SchedulingContext context) {
    this.allowance = luaTickLimit;
    this.context = checkNotNull(context, "context == null!");
  }

  @Override
  public void registerTicks(int ticks) {
    allowance -= ticks;
    context.registerTicks(ticks);
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
      return context.shouldPause();
    }
  }

  public int getAllowance() {
    return allowance;
  }

  public abstract boolean isAutosleep();

  public abstract void setAutosleep(boolean autosleep);
}
