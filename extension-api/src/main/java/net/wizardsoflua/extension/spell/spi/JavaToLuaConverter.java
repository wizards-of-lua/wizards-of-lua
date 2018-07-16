package net.wizardsoflua.extension.spell.spi;

import net.wizardsoflua.extension.api.Named;

public interface JavaToLuaConverter<J> extends SpellExtension, Named {
  Class<J> getJavaClass();

  Object getLuaInstance(J javaInstance);

  @SuppressWarnings({"rawtypes", "unchecked"})
  static Class<JavaToLuaConverter<?>> getClassWithWildcards() {
    return (Class) JavaToLuaConverter.class;
  }
}
