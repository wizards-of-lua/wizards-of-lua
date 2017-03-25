package net.karneim.luamod.lua.wrapper;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.sandius.rembulan.Table;

public class StringXLuaObjectMapInstance extends DelegatingTableWrapper<Map<String, Object>> {
  public StringXLuaObjectMapInstance(Table env, @Nullable Map<String, Object> delegate, Table metatable) {
    super(env, delegate, metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    for (Entry<String, Object> entry : delegate.entrySet()) {
      Object luaValue = entry.getValue();
      builder.addNullable(entry.getKey(), luaValue);
    }
  }
}
