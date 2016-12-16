package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.ImmutableTable;
import net.sandius.rembulan.impl.ImmutableTable.Builder;

public abstract class StructuredLuaWrapper<JavaObject> extends LuaWrapper<JavaObject> {

  public StructuredLuaWrapper(@Nullable JavaObject delegate) {
    super(delegate);
  }

  @Override
  protected final Table toLuaObject() {
    ImmutableTable.Builder builder = new ImmutableTable.Builder();
    toLuaObject(builder);
    return builder.build();
  }

  protected void toLuaObject(Builder builder) {}

}
