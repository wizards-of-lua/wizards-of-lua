package net.wizardsoflua.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target(TYPE)
public @interface GenerateLuaDoc {
  // TODO Adrodoc55 10.04.2018: Hier sollte kein default sein
  String name() default "";

  /**
   * The subtitle of the module.
   */
  String subtitle() default "";
}
