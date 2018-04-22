package net.wizardsoflua.extension.spell.api;

public interface ParallelTaskFactory {
  boolean isFinished();

  void terminate();
}
