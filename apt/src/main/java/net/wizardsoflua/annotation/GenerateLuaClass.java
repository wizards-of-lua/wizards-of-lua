package net.wizardsoflua.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target(TYPE)
public @interface GenerateLuaClass {
  /**
   * The name of the lua class.
   */
  String name();
}
