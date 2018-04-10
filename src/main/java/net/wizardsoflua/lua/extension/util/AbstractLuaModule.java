package net.wizardsoflua.lua.extension.util;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.lua.extension.api.Named;

@Deprecated
public abstract class AbstractLuaModule implements LuaTableExtension {
  @Override
  public abstract Table getTable();

  protected <F extends LuaFunction & Named> void add(F function) {
    Table table = getTable();
    String name = function.getName();
    table.rawset(name, function);
  }
}
