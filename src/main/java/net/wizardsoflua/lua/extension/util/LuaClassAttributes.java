package net.wizardsoflua.lua.extension.util;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.wizardsoflua.lua.classes.ObjectClass2;

@Retention(RUNTIME)
@Target(TYPE)
public @interface LuaClassAttributes {
  String name();

  Class<? extends LuaClass> superClass() default ObjectClass2.class;
}
