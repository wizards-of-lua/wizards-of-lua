package net.wizardsoflua.common;

public interface CheckedRunnable<T extends Throwable> {
  void run() throws T;
}
