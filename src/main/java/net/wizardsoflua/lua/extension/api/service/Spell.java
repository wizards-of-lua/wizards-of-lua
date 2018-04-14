package net.wizardsoflua.lua.extension.api.service;

import net.wizardsoflua.lua.extension.api.ParallelTaskFactory;

public interface Spell {
  void addParallelTaskFactory(ParallelTaskFactory factory);
}
