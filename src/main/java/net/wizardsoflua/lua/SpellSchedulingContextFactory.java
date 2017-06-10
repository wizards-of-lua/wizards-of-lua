package net.wizardsoflua.lua;

import net.sandius.rembulan.runtime.SchedulingContext;
import net.sandius.rembulan.runtime.SchedulingContextFactory;

public class SpellSchedulingContextFactory implements SchedulingContextFactory {

  @Override
  public SchedulingContext newInstance() {
    return new SpellSchedulingContext();
  }

}
