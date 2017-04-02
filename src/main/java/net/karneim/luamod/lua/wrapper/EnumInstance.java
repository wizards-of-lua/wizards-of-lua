package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.wrapper.LuaWrapper;
import net.sandius.rembulan.ByteString;

public class EnumInstance extends LuaWrapper<Enum<?>, ByteString> {
  public EnumInstance(LuaTypesRepo repo, @Nullable Enum<?> delegate) {
    super(repo, delegate);
  }

  @Override
  protected ByteString toLuaObject() {
    return ByteString.of(delegate.name());
  }
}
