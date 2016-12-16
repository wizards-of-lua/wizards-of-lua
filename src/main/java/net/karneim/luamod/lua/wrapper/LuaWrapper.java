package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

public abstract class LuaWrapper<E> {
  protected final @Nullable E delegate;
  private Object luaObject = null;

  public LuaWrapper(@Nullable E delegate) {
    this.delegate = delegate;
  }

  public @Nullable E getJavaObject() {
    return delegate;
  }

  public @Nullable Object getLuaObject() {
    if (delegate == null) {
      return null;
    }
    if (luaObject == null) {
      luaObject = toLuaObject();
    }
    return luaObject;
  }

  protected abstract Object toLuaObject();

}
