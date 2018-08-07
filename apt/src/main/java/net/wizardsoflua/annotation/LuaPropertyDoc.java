package net.wizardsoflua.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target(METHOD)
public @interface LuaPropertyDoc {
  /**
   * The type of the property. Defaults to a value appropriate for the return / parameter type.
   */
  String type() default "";
}
