package net.wizardsoflua.testenv.junit;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;

public class TestResults extends RunNotifier {
  private final Logger logger;
  private int testsFinished = 0;
  private List<Failure> failures = new ArrayList<>();

  public TestResults(Logger logger) {
    this.logger = checkNotNull(logger, "logger==null!");
  }

  @Override
  public void fireTestStarted(Description description) throws StoppedByUserException {
    super.fireTestStarted(description);
    logger.info("Running "+description.getDisplayName());
  }

  @Override
  public void fireTestFailure(Failure failure) {
    super.fireTestFailure(failure);
    this.failures.add(failure);
    logger.info("Failed "+failure.getTestHeader());
    logger.error(failure.getTrace());
  }

  @Override
  public void fireTestFinished(Description description) {
    super.fireTestFinished(description);
    testsFinished++;
    logger.info("Finished "+description.getDisplayName());
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
