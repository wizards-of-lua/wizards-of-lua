package net.wizardsoflua.lua.extension.api;

public interface ParallelTaskFactory {
  boolean isFinished();

  void terminate();
}
