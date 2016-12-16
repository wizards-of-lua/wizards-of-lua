package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

public abstract class LuaWrapper<JavaObject> {
  protected final @Nullable JavaObject delegate;
  private Object luaObject = null;

  public LuaWrapper(@Nullable JavaObject delegate) {
    this.delegate = delegate;
  }

  public @Nullable JavaObject getJavaObject() {
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
