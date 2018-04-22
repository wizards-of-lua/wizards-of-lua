package net.wizardsoflua.extension.spell.api.resource;

import net.wizardsoflua.extension.spell.api.ParallelTaskFactory;

public interface Spell {
  void addParallelTaskFactory(ParallelTaskFactory factory);
}
