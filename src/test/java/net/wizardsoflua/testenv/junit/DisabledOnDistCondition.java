package net.wizardsoflua.testenv.junit;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class DisabledOnDistCondition implements ExecutionCondition {
  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    Optional<AnnotatedElement> element = context.getElement();
    Optional<DisabledOnDist> annotation =
        AnnotationSupport.findAnnotation(element, DisabledOnDist.class);
    if (annotation.isPresent()) {
      DisabledOnDist disabledOnDist = annotation.get();
      Dist dist = disabledOnDist.value();
      Boolean isOnDist = DistExecutor.callWhenOn(dist, () -> () -> true);
      if (isOnDist != null && isOnDist) {
        String reason = disabledOnDist.reason();
        if (reason.isEmpty()) {
          reason = element.get() + " is @DisabledOnDist(" + dist + ")";
        }
        return ConditionEvaluationResult.disabled(reason);
      } else {
        return ConditionEvaluationResult.enabled("Current dist is not " + dist);
      }
    } else {
      return ConditionEvaluationResult.enabled("@DisabledOnDist is not present");
    }
  }
}
