package net.wizardsoflua.extension.spell.spi;

public interface LuaConverter<J, L> extends SpellExtension {
  Class<J> getJavaClass();

  Class<L> getLuaClass();

  J getJavaInstance(L luaInstance);

  L getLuaInstance(J javaInstance);
}
