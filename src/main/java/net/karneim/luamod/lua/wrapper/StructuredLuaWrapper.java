package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.DynamicTable;
import net.sandius.rembulan.Table;

public abstract class StructuredLuaWrapper<E> extends LuaWrapper<E> {

  public StructuredLuaWrapper(@Nullable E delegate) {
    super(delegate);
  }

  @Override
  protected final Table toLuaObject() {
    DynamicTable.Builder builder = new DynamicTable.Builder(delegate);
    addProperties(builder);
    DynamicTable result = builder.build();
    return result;
  }

  protected void addProperties(DynamicTable.Builder builder) {}

}
