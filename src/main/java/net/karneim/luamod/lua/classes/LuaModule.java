package net.karneim.luamod.lua.classes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LuaModule {
  /**
   * The package name of the lua module. This is the package name of the annotated class by default.
   *
   * @return the package name of the lua module
   */
  String packageName() default "";

  /**
   * The name of the lua module.
   *
   * @return the name of the lua module
   */
  String value();
}
