package net.wizardsoflua.extension.spell.api.resource;

import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.extension.spell.api.ParallelTaskFactory;

public interface Spell {
  void addParallelTaskFactory(ParallelTaskFactory factory);

  void addThread(String name, LuaFunction function, Object... args);
}
