package net.wizardsoflua.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This {@link Annotation} can be used as an alternative to {@link GenerateLuaClass} if you don't
 * want to generate the lua class and lua proxy, but write them from hand. This {@link Annotation}
 * is used to find the super class and super proxy of generated sub classes.
 *
 * @author Adrodoc55
 */
@Retention(SOURCE)
@Target(TYPE)
public @interface HasLuaClass {
  Class<?> luaClass();

  String LUA_CLASS = "luaClass";

  Class<?> luaProxy();

  String LUA_PROXY = "luaProxy";
}
