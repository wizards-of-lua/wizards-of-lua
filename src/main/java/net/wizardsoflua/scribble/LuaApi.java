package net.wizardsoflua.scribble;

import static java.util.Objects.requireNonNull;

import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

public class LuaApi<D> {
  protected final ProxyingLuaClass<?, ?> luaClass;
  protected final D delegate;

  public LuaApi(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    this.luaClass = requireNonNull(luaClass, "luaClass == null!");
    this.delegate = requireNonNull(delegate, "delegate == null!");
  }

  public ProxyingLuaClass<?, ?> getLuaClass() {
    return luaClass;
  }

  public D getDelegate() {
    return delegate;
  }

  public Converters getConverters() {
    return luaClass.getClassLoader().getConverters();
  }
}
