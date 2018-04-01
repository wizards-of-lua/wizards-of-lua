package net.wizardsoflua.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target({METHOD, TYPE})
public @interface LuaFunction {
  /**
   * The name of the function, defaults to the name of the annotated method. This must be specified
   * when annotating an inner class.
   */
  String name() default "";
}
