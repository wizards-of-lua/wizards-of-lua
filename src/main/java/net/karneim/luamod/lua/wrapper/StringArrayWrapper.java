package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;

public class StringArrayWrapper extends LuaWrapper<String[]> {
  public StringArrayWrapper(@Nullable String[] delegate) {
    super(delegate);
  }

  @Override
  protected Object toLuaObject() {
    Table luaTable = DefaultTable.factory().newTable();
    if (delegate != null) {
      int idx = 0;
      for (String value : delegate) {
        idx++;
        luaTable.rawset(idx, value);
      }
    }
    return luaTable;
  }
}
