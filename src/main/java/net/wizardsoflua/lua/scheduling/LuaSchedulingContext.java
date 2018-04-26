package net.wizardsoflua.lua.scheduling;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import net.sandius.rembulan.LuaRuntimeException;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.SchedulingContext;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.extension.spell.api.resource.LuaScheduler;

public abstract class LuaSchedulingContext implements SchedulingContext {
  public interface Context {
    void registerTicks(int ticks);
  }

  private final Context context;
  private long allowance;
  /**
   * This is set by {@link LuaScheduler#sleep(ExecutionContext, int)} and will cause a
   * {@link CallFellAsleepException} if greater than zero.
   */
  private int sleepDuration;

  public LuaSchedulingContext(long luaTickLimit, Context context) {
    this.context = checkNotNull(context, "context == null!");
    this.allowance = luaTickLimit;
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
        throw new LuaRuntimeException(
            "Spell has been broken automatically since it has exceeded its tick allowance!");
      }
    } else {
      return false;
    }
  }

  public long getAllowance() {
    return allowance;
  }

  public int getSleepDuration() {
    return sleepDuration;
  }

  public void setSleepDuration(int sleepDuration) {
    checkArgument(sleepDuration >= 0, "Can't sleep a negative amount of time");
    this.sleepDuration = sleepDuration;
  }

  public abstract boolean isAutosleep();

  public abstract void setAutosleep(boolean autosleep);

  public abstract void pause(ExecutionContext context) throws UnresolvedControlThrowable;

  public abstract void pauseIfRequested(ExecutionContext context) throws UnresolvedControlThrowable;
}
