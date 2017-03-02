package net.karneim.luamod.lua.wrapper;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.sandius.rembulan.Table;

public class StringXLuaObjectMapWrapper extends DelegatingTableWrapper<Map<String, Object>> {
  public StringXLuaObjectMapWrapper(Table env, @Nullable Map<String, Object> delegate) {
    super(env, delegate);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    for (Entry<String, Object> entry : delegate.entrySet()) {
      Object luaValue = entry.getValue();
      builder.addNullable(entry.getKey(), luaValue);
    }
  }
}
