package net.wizardsoflua.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target(METHOD)
public @interface LuaProperty {
  /**
   * The name of the property. Defaults to the lowercase method name without getter or setter
   * prefix.
   */
  String name() default "";

  /**
   * The type of the property. Defaults to a value appropriate for the return / parameter type.
   */
  String type() default "";
}
