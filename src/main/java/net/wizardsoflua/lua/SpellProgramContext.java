package net.wizardsoflua.lua;

import net.sandius.rembulan.runtime.SchedulingContextFactory;
import net.wizardsoflua.lua.runtime.Runtime;

public interface SpellProgramContext {

  SchedulingContextFactory getSchedulingContextFactory();

  Runtime getRuntime();

}
