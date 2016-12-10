package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;

public class GenericLuaEventWrapper extends EventWrapper<Object> {
  public GenericLuaEventWrapper(@Nullable Object delegate, String name) {
    super(delegate, name);
  }

  @Override
  protected Table toLuaObject() {
    Table result = super.toLuaObject();
    result.rawset("content", delegate);
    return result;
  }
}
