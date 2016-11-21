package net.karneim.luamod.lua;

import javax.annotation.Nullable;

import net.sandius.rembulan.LuaObject;

public abstract class LuaWrapper<JavaObject> {
  protected final @Nullable JavaObject delegate;

  public LuaWrapper(@Nullable JavaObject delegate) {
    this.delegate = delegate;
  }

  public @Nullable JavaObject getJavaObject() {
    return delegate;
  }

  public @Nullable LuaObject getLuaObject() {
    if (delegate == null) {
      return null;
    }
    return toLuaObject();
  }

  protected abstract LuaObject toLuaObject();
}
