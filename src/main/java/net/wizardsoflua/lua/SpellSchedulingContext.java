package net.wizardsoflua.lua;

import net.sandius.rembulan.runtime.SchedulingContext;

public class SpellSchedulingContext implements SchedulingContext {

  public SpellSchedulingContext() {}

  @Override
  public void registerTicks(int consumedTicks) {
    // TODO implement allowance
  }

  @Override
  public boolean shouldPause() {
    // TODO implement allowance
    return false;
  }
}
