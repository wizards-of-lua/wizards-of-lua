package net.wizardsoflua.lua.scheduling;

import net.sandius.rembulan.runtime.SchedulingContextFactory;

public interface LuaSchedulingContextFactory extends SchedulingContextFactory {
  @Override
  LuaSchedulingContext newInstance();
}
