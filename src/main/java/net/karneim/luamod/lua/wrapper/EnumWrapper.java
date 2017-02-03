package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.wrapper.LuaWrapper;
import net.sandius.rembulan.ByteString;

public class EnumWrapper extends LuaWrapper<Enum<?>, ByteString> {
  public EnumWrapper(@Nullable Enum<?> delegate) {
    super(delegate);
  }

  @Override
  protected ByteString toLuaObject() {
    return ByteString.of(delegate.name());
  }
}
