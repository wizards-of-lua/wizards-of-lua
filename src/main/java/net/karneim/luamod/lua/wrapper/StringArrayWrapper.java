package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.DynamicTable;



public class StringArrayWrapper extends StructuredLuaWrapper<String[]> {
  public StringArrayWrapper(@Nullable String[] delegate) {
    super(delegate);
  }

  @Override
  protected void addProperties(DynamicTable.Builder builder) {
    super.addProperties(builder);
    int idx = 0;
    for (String value : delegate) {
      idx++;
      builder.add(idx, value);
    }
  }
}
