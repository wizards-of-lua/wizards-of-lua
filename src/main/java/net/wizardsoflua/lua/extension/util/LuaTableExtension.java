package net.wizardsoflua.lua.extension.util;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.extension.spi.LuaExtension;

public interface LuaTableExtension extends LuaExtension {
  @Override
  default void installInto(Table env) {
    String name = getName();
    Table table = createTable();
    env.rawset(name, table);
  }

  String getName();

  Table createTable();
}
