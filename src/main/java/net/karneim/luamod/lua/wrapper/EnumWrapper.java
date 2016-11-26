package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

public class EnumWrapper extends LuaWrapper<Enum<?>> {
  public EnumWrapper(@Nullable Enum<?> delegate) {
    super(delegate);
  }

  @Override
  protected Object toLuaObject() {
    return delegate == null? null: delegate.name();
  }
}
