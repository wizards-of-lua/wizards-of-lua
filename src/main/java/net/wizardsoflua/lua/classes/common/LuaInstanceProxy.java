package net.wizardsoflua.lua.classes.common;

import static com.google.common.base.Preconditions.checkNotNull;

import net.wizardsoflua.lua.classes.ProxyingLuaClass;

public abstract class LuaInstanceProxy<D> extends DelegatingProxy<D> {
  protected final ProxyingLuaClass<?, ?> luaClass;
  protected D delegate;

  public LuaInstanceProxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass.getClassLoader(), luaClass.getMetaTable(), delegate);
    this.luaClass = checkNotNull(luaClass, "luaClass == null!");
    this.delegate = checkNotNull(delegate, "delegate==null!");
  }

  public ProxyingLuaClass<?, ?> getLuaClass() {
    return luaClass;
  }
}
