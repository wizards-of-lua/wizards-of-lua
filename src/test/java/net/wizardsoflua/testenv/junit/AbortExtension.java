package net.wizardsoflua.testenv.junit;

import static org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;

public class AbortExtension implements ExecutionCondition {
  private static final ConditionEvaluationResult DISABLED = disabled("Test run aborted");
  private static final ConditionEvaluationResult ENABLED = enabled("Test run not aborted");
  private volatile boolean abort;

  /**
   * Signal JUnit to abort the test run. This requires {@code this} {@link Extension} to be
   * registered via {@link RegisterExtension}.
   */
  public void abortTestRun() {
    abort = true;
  }

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    if (abort) {
      return DISABLED;
    } else {
      return ENABLED;
    }
  }
}
