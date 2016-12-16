package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.ImmutableTable;
import net.sandius.rembulan.impl.ImmutableTable.Builder;

public abstract class StructuredLuaWrapper<E> extends LuaWrapper<E> {

  public StructuredLuaWrapper(@Nullable E delegate) {
    super(delegate);
  }

  @Override
  protected final Table toLuaObject() {
    ImmutableTable.Builder builder = new ImmutableTable.Builder();
    addProperties(builder);
    ImmutableTable result = builder.build();
    return result;
  }

  protected void addProperties(Builder builder) {}

}
