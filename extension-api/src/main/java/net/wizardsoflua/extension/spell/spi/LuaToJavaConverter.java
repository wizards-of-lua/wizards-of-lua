package net.wizardsoflua.extension.spell.spi;

import net.wizardsoflua.extension.api.Named;

public interface LuaToJavaConverter<J, L> extends SpellExtension, Named {
  Class<J> getJavaClass();

  Class<L> getLuaClass();

  J getJavaInstance(L luaInstance);

  @SuppressWarnings({"rawtypes", "unchecked"})
  static Class<LuaToJavaConverter<?, ?>> getClassWithWildcards() {
    return (Class) LuaToJavaConverter.class;
  }
}
