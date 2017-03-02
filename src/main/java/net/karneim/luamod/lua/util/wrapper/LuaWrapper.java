package net.karneim.luamod.lua.util.wrapper;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;

public abstract class LuaWrapper<J, L> {
  protected final @Nullable J delegate;
  private @Nullable L luaObject;
  protected Table env;

  public LuaWrapper(Table env, @Nullable J delegate) {
    this.delegate = delegate;
    this.env = env;
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
