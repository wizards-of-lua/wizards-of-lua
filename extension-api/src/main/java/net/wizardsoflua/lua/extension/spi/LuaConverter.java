package net.wizardsoflua.lua.extension.spi;

public interface LuaConverter<J, L> {
  Class<J> getJavaClass();

  Class<L> getLuaClass();

  J getJavaInstance(L luaInstance);

  L getLuaInstance(J javaInstance);
}
