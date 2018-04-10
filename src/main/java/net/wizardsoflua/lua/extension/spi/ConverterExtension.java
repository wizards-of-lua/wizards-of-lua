package net.wizardsoflua.lua.extension.spi;

import net.wizardsoflua.lua.extension.util.SpellExtension;

public interface ConverterExtension<J, L> extends SpellExtension {
  Class<J> getJavaClass();

  Class<L> getLuaClass();

  J getJavaInstance(L luaInstance);

  L getLuaInstance(J javaInstance);
}
