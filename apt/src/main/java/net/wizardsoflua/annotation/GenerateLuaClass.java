package net.wizardsoflua.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(TYPE)
public @interface GenerateLuaClass {
  /**
   * The name of the lua class.
   */
  String name();

  Class<?> superClass();
}
