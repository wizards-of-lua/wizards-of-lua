package net.karneim.luamod.lua.wrapper;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;

public class StringXLuaObjectMapWrapper extends StructuredLuaWrapper<Map<String, Object>> {
  public StringXLuaObjectMapWrapper(@Nullable Map<String, Object> delegate) {
    super(delegate);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    for (Entry<String, Object> entry : delegate.entrySet()) {
      Object luaValue = entry.getValue();
      builder.add(entry.getKey(), luaValue);
    }
  }
}
