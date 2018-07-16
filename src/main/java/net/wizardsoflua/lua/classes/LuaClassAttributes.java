package net.wizardsoflua.lua.classes;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface LuaClassAttributes {
  String name();

  Class<? extends LuaClass> superClass() default ObjectClass.class;
}
