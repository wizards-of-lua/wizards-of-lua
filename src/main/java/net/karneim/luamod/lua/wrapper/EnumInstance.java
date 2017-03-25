package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.wrapper.LuaWrapper;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;

public class EnumInstance extends LuaWrapper<Enum<?>, ByteString> {
  public EnumInstance(Table env, @Nullable Enum<?> delegate) {
    super(env, delegate);
  }

  @Override
  protected ByteString toLuaObject() {
    return ByteString.of(delegate.name());
  }
}
