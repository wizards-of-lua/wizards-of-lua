package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

public abstract class LuaWrapper<JavaObject> {
  protected final @Nullable JavaObject delegate;

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
    return toLuaObject();
  }

  protected abstract Object toLuaObject();
}
