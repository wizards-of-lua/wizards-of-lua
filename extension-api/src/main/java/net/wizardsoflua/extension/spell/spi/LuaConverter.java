package net.wizardsoflua.extension.spell.spi;

import net.wizardsoflua.extension.api.Named;

public interface LuaConverter<J, L> extends SpellExtension, Named {
  Class<J> getJavaClass();

  Class<L> getLuaClass();

  J getJavaInstance(L luaInstance);

  L getLuaInstance(J javaInstance);
}
