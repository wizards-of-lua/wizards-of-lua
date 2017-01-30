package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.LuaWrapper;

public abstract class StructuredLuaWrapper<E> extends LuaWrapper<E, DelegatingTable> {

  public StructuredLuaWrapper(@Nullable E delegate) {
    super(delegate);
  }

  @Override
  protected final DelegatingTable toLuaObject() {
    DelegatingTable.Builder builder = new DelegatingTable.Builder(delegate);
    addProperties(builder);
    return builder.build();
  }

  protected void addProperties(DelegatingTable.Builder builder) {}

}
