package net.wizardsoflua.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(METHOD)
public @interface LuaProperty {
  /**
   * The name of the property. Defaults to the lowercase method name without getter or setter
   * prefix.
   */
  String name() default "";
}
