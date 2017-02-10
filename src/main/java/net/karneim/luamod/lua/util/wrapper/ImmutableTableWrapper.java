package net.karneim.luamod.lua.util.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.patched.PatchedImmutableTable;

public abstract class ImmutableTableWrapper<J> extends LuaWrapper<J, PatchedImmutableTable> {
  public ImmutableTableWrapper(@Nullable J delegate) {
    super(delegate);
  }

  @Override
  protected final PatchedImmutableTable toLuaObject() {
    PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();
    addProperties(builder);
    return builder.build();
  }

  protected abstract void addProperties(PatchedImmutableTable.Builder builder);
}
