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
    return false;
  }
}
