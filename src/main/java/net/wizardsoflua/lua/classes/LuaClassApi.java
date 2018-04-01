package net.wizardsoflua.lua.classes;

import static java.util.Objects.requireNonNull;

import net.wizardsoflua.lua.module.LuaModuleApi;

public class LuaClassApi<D> extends LuaModuleApi<D> {
  protected final ProxyingLuaClass<?, ?> luaClass;

  public LuaClassApi(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass.getClassLoader(), delegate);
    this.luaClass = requireNonNull(luaClass, "luaClass == null!");
  }

  public ProxyingLuaClass<?, ?> getLuaClass() {
    return luaClass;
  }

  @Override
  public boolean isTransferable() {
    return true;
  }
}
