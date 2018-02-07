package net.wizardsoflua.annotation.lua;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(TYPE)
public @interface LuaModule {
  /**
   * The name of the module. Defaults to the simple class name.
   */
  String name() default "";
}
