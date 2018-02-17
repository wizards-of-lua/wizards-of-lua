package net.wizardsoflua.lua.classes.common;

import static com.google.common.base.Preconditions.checkNotNull;

import net.wizardsoflua.lua.classes.ProxyingLuaClass;

public abstract class LuaInstance<D> extends DelegatingProxy<D> {
  protected final ProxyingLuaClass<?, ?> luaClass;

  public LuaInstance(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass.getClassLoader(), delegate);
    this.luaClass = checkNotNull(luaClass, "luaClass == null!");
    setMetatable(luaClass.getMetaTable());
  }

  public ProxyingLuaClass<?, ?> getLuaClass() {
    return luaClass;
  }
}
