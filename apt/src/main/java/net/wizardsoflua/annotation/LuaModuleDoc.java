package net.wizardsoflua.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target(TYPE)
public @interface LuaModuleDoc {
  /**
   * The subtitle of the module. Defaults to "".
   */
  String subtitle() default "";

  /**
   * The type of module, for instance "module" or "class".
   */
  String type();

  /**
   * The description of the module. Defaults to the javadoc.
   */
  String description() default "";
}
