package net.wizardsoflua.lua.scheduling;

import net.sandius.rembulan.runtime.SchedulingContext;

public class SpellSchedulingContext implements SchedulingContext {

  private final SpellSchedulingConfig config;
  private int allowance;

  public SpellSchedulingContext(SpellSchedulingConfig config) {
    this.config = config;
    this.allowance = config.getLuaTicksLimit();
  }

  @Override
  public void registerTicks(int consumedTicks) {
    this.allowance -= consumedTicks;
  }

  @Override
  public boolean shouldPause() {
    if (config.isAutoSleep()) {
      return allowance <= 0;
    }
    if (allowance < 0) {
      throw new IllegalStateException(
          "Spell has been broken automatically since it has exceeded its tick allowance!");
    }
    return false;
  }
}
