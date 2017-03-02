package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.sandius.rembulan.Table;



public class StringArrayWrapper extends DelegatingTableWrapper<String[]> {
  public StringArrayWrapper(Table env, @Nullable String[] delegate) {
    super(env, delegate);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    int idx = 0;
    for (String value : delegate) {
      idx++;
      builder.addNullable(idx, value);
    }
  }
}
