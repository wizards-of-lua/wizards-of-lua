package net.wizardsoflua.lua;

import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import net.wizardsoflua.extension.spell.spi.JavaToLuaConverter;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.extension.spell.spi.LuaToJavaConverter;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.extension.ServiceLoader;

public class ExtensionLoader {
  private static @Nullable ImmutableSet<Class<? extends LuaConverter<?, ?>>> lUA_CONVERTERS;

  private static Set<Class<? extends LuaConverter<?, ?>>> getLuaConverters() {
    if (lUA_CONVERTERS == null) {
      Builder<Class<? extends LuaConverter<?, ?>>> builder = ImmutableSet.builder();
      builder.addAll(ServiceLoader.load(LuaConverter.getClassWithWildcards()));
      lUA_CONVERTERS = builder.build();
    }
    return lUA_CONVERTERS;
  }

  private static @Nullable ImmutableSet<Class<? extends LuaToJavaConverter<?, ?>>> LUA_TO_JAVA_CONVERTERS;

  public static ImmutableSet<Class<? extends LuaToJavaConverter<?, ?>>> getLuaToJavaConverters() {
    if (LUA_TO_JAVA_CONVERTERS == null) {
      Builder<Class<? extends LuaToJavaConverter<?, ?>>> builder = ImmutableSet.builder();
      builder.addAll(ServiceLoader.load(LuaToJavaConverter.getClassWithWildcards()));
      builder.addAll(getLuaConverters());
      LUA_TO_JAVA_CONVERTERS = builder.build();
    }
    return LUA_TO_JAVA_CONVERTERS;
  }

  private static @Nullable ImmutableSet<Class<? extends JavaToLuaConverter<?>>> JAVA_TO_LUA_CONVERTERS;

  public static ImmutableSet<Class<? extends JavaToLuaConverter<?>>> getJavaToLuaConverters() {
    if (JAVA_TO_LUA_CONVERTERS == null) {
      Builder<Class<? extends JavaToLuaConverter<?>>> builder = ImmutableSet.builder();
      builder.addAll(ServiceLoader.load(JavaToLuaConverter.getClassWithWildcards()));
      builder.addAll(getLuaConverters());
      JAVA_TO_LUA_CONVERTERS = builder.build();
    }
    return JAVA_TO_LUA_CONVERTERS;
  }

  private static @Nullable ImmutableSet<Class<? extends SpellExtension>> SPELL_EXTENSIONS;

  public static ImmutableSet<Class<? extends SpellExtension>> getSpellExtensions() {
    if (SPELL_EXTENSIONS == null) {
      Builder<Class<? extends SpellExtension>> builder = ImmutableSet.builder();
      builder.addAll(ServiceLoader.load(SpellExtension.class));
      builder.addAll(getLuaToJavaConverters());
      builder.addAll(getJavaToLuaConverters());
      SPELL_EXTENSIONS = builder.build();
    }
    return SPELL_EXTENSIONS;
  }
}
