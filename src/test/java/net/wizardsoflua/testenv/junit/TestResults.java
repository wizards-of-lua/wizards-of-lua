package net.wizardsoflua.testenv.junit;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class TestResults extends RunNotifier {
  private int testsFinished = 0;
  private List<Failure> failures = new ArrayList<>();

  @Override
  public void fireTestFailure(Failure failure) {
    this.failures.add(failure);
  }

  @Override
  public void fireTestFinished(Description description) {
    testsFinished++;
  }

  public boolean isOK() {
    return failures.isEmpty();
  }

  public Iterable<Failure> getFailures() {
    return failures;
  }

  public int getTestsFinished() {
    return testsFinished;
  }
}
