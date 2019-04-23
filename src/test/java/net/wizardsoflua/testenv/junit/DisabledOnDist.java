package net.wizardsoflua.testenv.junit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import net.minecraftforge.api.distmarker.Dist;

@Documented
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface DisabledOnDist {
  Dist value();

  /**
   * The reason this annotated test class or test method is disabled.
   */
  String reason() default "";
}
