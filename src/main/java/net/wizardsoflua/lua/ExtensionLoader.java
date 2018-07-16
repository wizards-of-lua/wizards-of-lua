package net.wizardsoflua.lua;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableSet;

import net.wizardsoflua.extension.spell.spi.JavaToLuaConverter;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.extension.spell.spi.LuaToJavaConverter;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.extension.ServiceLoader;

public class ExtensionLoader {
  private static @Nullable ImmutableSet<Class<? extends LuaToJavaConverter<?, ?>>> LUA_TO_JAVA_CONVERTERS;
  private static @Nullable ImmutableSet<Class<? extends JavaToLuaConverter<?>>> JAVA_TO_LUA_CONVERTERS;
  private static @Nullable ImmutableSet<Class<? extends SpellExtension>> SPELL_EXTENSION;

  public static void initialize(Logger logger) {
    Set<Class<? extends LuaConverter<?, ?>>> converters =
        ServiceLoader.load(logger, LuaConverter.getClassWithWildcards());

    Set<Class<? extends LuaToJavaConverter<?, ?>>> luaToJava = new HashSet<>(converters);
    luaToJava.addAll(ServiceLoader.load(logger, LuaToJavaConverter.getClassWithWildcards()));
    LUA_TO_JAVA_CONVERTERS = ImmutableSet.copyOf(luaToJava);

    Set<Class<? extends JavaToLuaConverter<?>>> javaToLua = new HashSet<>(converters);
    javaToLua.addAll(ServiceLoader.load(logger, JavaToLuaConverter.getClassWithWildcards()));
    JAVA_TO_LUA_CONVERTERS = ImmutableSet.copyOf(javaToLua);

    Set<Class<? extends SpellExtension>> spellExtensions =
        new HashSet<>(ServiceLoader.load(logger, SpellExtension.class));
    spellExtensions.addAll(LUA_TO_JAVA_CONVERTERS);
    spellExtensions.addAll(JAVA_TO_LUA_CONVERTERS);
    SPELL_EXTENSION = ImmutableSet.copyOf(spellExtensions);
  }

  public static ImmutableSet<Class<? extends LuaToJavaConverter<?, ?>>> getLuaToJavaConverters()
      throws IllegalStateException {
    if (LUA_TO_JAVA_CONVERTERS == null) {
      throw throwNotInitializedException();
    }
    return LUA_TO_JAVA_CONVERTERS;
  }

  public static ImmutableSet<Class<? extends JavaToLuaConverter<?>>> getJavaToLuaConverters()
      throws IllegalStateException {
    if (JAVA_TO_LUA_CONVERTERS == null) {
      throw throwNotInitializedException();
    }
    return JAVA_TO_LUA_CONVERTERS;
  }

  public static ImmutableSet<Class<? extends SpellExtension>> getSpellExtension()
      throws IllegalStateException {
    if (SPELL_EXTENSION == null) {
      throw throwNotInitializedException();
    }
    return SPELL_EXTENSION;
  }

  private static IllegalStateException throwNotInitializedException() throws IllegalStateException {
    throw new IllegalStateException(
        ExtensionLoader.class.getSimpleName() + " is not initialized yet");
  }
}
