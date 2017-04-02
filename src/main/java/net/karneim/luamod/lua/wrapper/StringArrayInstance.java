package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.sandius.rembulan.Table;

public class StringArrayInstance extends DelegatingTableWrapper<String[]> {
  public StringArrayInstance(LuaTypesRepo repo, @Nullable String[] delegate, Table metatable) {
    super(repo, delegate, metatable);
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
