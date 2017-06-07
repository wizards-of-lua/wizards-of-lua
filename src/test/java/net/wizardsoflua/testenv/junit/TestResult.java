package net.wizardsoflua.testenv.junit;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class TestResult extends RunNotifier {
  private Failure failure;

  @Override
  public void fireTestFailure(Failure failure) {
    this.failure = failure;
  }

  public boolean isOK() {
    return failure == null;
  }

  public Failure getFailure() {
    return failure;
  }

}
