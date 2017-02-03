package net.karneim.luamod.lua.util.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;

public abstract class DelegatingTableWrapper<J> extends LuaWrapper<J, DelegatingTable> {
  public DelegatingTableWrapper(@Nullable J delegate) {
    super(delegate);
  }

  @Override
  protected final DelegatingTable toLuaObject() {
    DelegatingTable.Builder builder = DelegatingTable.builder(delegate);
    addProperties(builder);
    return builder.build();
  }

  protected abstract void addProperties(DelegatingTable.Builder builder);
}
