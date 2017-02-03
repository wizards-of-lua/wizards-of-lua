package net.karneim.luamod.lua.util.wrapper;

import javax.annotation.Nullable;

import net.sandius.rembulan.impl.ImmutableTable;

public abstract class ImmutableTableWrapper<J> extends LuaWrapper<J, ImmutableTable> {
  public ImmutableTableWrapper(@Nullable J delegate) {
    super(delegate);
  }

  @Override
  protected final ImmutableTable toLuaObject() {
    ImmutableTable.Builder builder = new ImmutableTable.Builder();
    addProperties(builder);
    return builder.build();
  }

  protected abstract void addProperties(ImmutableTable.Builder builder);
}
