package net.karneim.luamod.lua.util.wrapper;

import javax.annotation.Nullable;

public abstract class LuaWrapper<J, L> {
  protected final @Nullable J delegate;
  private @Nullable L luaObject;

  public LuaWrapper(@Nullable J delegate) {
    this.delegate = delegate;
  }

  public @Nullable J getJavaObject() {
    return delegate;
  }

  public @Nullable L getLuaObject() {
    if (delegate == null) {
      return null;
    }
    if (luaObject == null) {
      luaObject = toLuaObject();
    }
    return luaObject;
  }

  protected abstract L toLuaObject();
}
