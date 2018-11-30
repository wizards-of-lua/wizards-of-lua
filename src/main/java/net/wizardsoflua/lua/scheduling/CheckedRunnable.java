package net.wizardsoflua.lua.scheduling;

public interface CheckedRunnable {
  CheckedRunnable DO_NOTHING = () -> {
  };

  void run() throws Exception;
}