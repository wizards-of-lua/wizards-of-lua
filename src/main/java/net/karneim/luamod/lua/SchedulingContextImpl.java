package net.karneim.luamod.lua;

import net.karneim.luamod.lua.event.Events;
import net.sandius.rembulan.runtime.SchedulingContext;
import net.sandius.rembulan.util.Check;

public class SchedulingContextImpl implements SchedulingContext {

  private final Ticks ticks;
  private final Events events;
  private final Runtime runtime;

  public SchedulingContextImpl(Events events, Ticks ticks, Runtime runtime) {
    this.events = events;
    this.ticks = ticks;
    this.runtime = runtime;
    this.ticks.resetAllowance();
  }

  @Override
  public void registerTicks(int consumedTicks) {
    ticks.addTicks(consumedTicks);
  }

  @Override
  public boolean shouldPause() {
    // if (allowance <= 0) {
    // return true;
    // }
    if (ticks.getAllowance() <= -ticks.getMax()) {
      throw new IllegalStateException(
          "Spell has been broken automatically since it is running for too many ticks!");
    }
    if (events.isWaitingForEvent()) {
      return true;
    }
    if (runtime.isSleeping()) {
      return true;
    }
    return false;
  }
}