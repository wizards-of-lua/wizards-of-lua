package net.wizardsoflua.lua.classes;

import static java.util.Objects.requireNonNull;

import net.wizardsoflua.lua.module.LuaModuleApi;

public class LuaClassApi<D> extends LuaModuleApi<D> {
  protected final DelegatorLuaClass<?, ?> luaClass;

  public LuaClassApi(DelegatorLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass.getClassLoader(), delegate);
    this.luaClass = requireNonNull(luaClass, "luaClass == null!");
  }

  public DelegatorLuaClass<?, ?> getLuaClass() {
    return luaClass;
  }

  @Override
  public boolean isTransferable() {
    return true;
  }
}
