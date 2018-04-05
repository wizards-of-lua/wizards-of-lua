package net.wizardsoflua.lua.extension.util;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.extension.api.Named;
import net.wizardsoflua.lua.extension.spi.LuaModule;

public abstract class AbstractLuaModule implements LuaModule {
  protected void add(Named named) {
    Table table = getTable();
    String name = named.getName();
    table.rawset(name, named);
  }
}
