package net.wizardsoflua.lua.extension.util;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.extension.api.Named;

public abstract class AbstractLuaModule implements AbstractLuaUtility {
  @Override
  public abstract Table getLuaObject();

  protected void add(Named named) {
    Table table = getLuaObject();
    String name = named.getName();
    table.rawset(name, named);
  }
}
