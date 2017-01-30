package net.karneim.luamod.lua.wrapper;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;

public class StringXLuaObjectMapWrapper extends DelegatingTableWrapper<Map<String, Object>> {
  public StringXLuaObjectMapWrapper(@Nullable Map<String, Object> delegate) {
    super(delegate);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    for (Entry<String, Object> entry : delegate.entrySet()) {
      Object luaValue = entry.getValue();
      builder.add(entry.getKey(), luaValue);
    }
  }
}
