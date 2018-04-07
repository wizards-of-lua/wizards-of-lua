package net.wizardsoflua.lua.extension.api;

import net.sandius.rembulan.runtime.SchedulingContext;

public interface Spell {
  void addParallelTaskFactory(ParallelTaskFactory factory);

  // TODO Adrodoc 07.04.2018: Use custom interface instead of SchedulingContext
  void addSchedulingContext(SchedulingContext context);
}
