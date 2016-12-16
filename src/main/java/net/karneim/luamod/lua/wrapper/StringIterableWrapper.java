package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.sandius.rembulan.impl.ImmutableTable;

public class StringIterableWrapper extends StructuredLuaWrapper<Iterable<String>> {
  public StringIterableWrapper(@Nullable Iterable<String> delegate) {
    super(delegate);
  }

  @Override
  protected void toLuaObject(ImmutableTable.Builder builder) {
    super.toLuaObject(builder);
    int idx = 0;
    for (String value : delegate) {
      idx++;
      builder.add(idx, value);
    }
  }
}
