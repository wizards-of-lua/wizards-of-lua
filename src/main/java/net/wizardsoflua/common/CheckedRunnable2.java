package net.wizardsoflua.common;

public interface CheckedRunnable2<T1 extends Throwable, T2 extends Throwable> {
  void run() throws T1, T2;
}
