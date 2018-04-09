package net.wizardsoflua.lua.classes.common;

import static com.google.common.base.Preconditions.checkNotNull;

import net.wizardsoflua.lua.classes.DelegatorLuaClass;

public abstract class LuaInstance<D> extends DelegatingProxy<D> {
  protected final DelegatorLuaClass<?, ?> luaClass;

  public LuaInstance(DelegatorLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass.getClassLoader(), delegate);
    this.luaClass = checkNotNull(luaClass, "luaClass == null!");
    setMetatable(luaClass.getMetaTable());
  }

  public DelegatorLuaClass<?, ?> getLuaClass() {
    return luaClass;
  }
}
