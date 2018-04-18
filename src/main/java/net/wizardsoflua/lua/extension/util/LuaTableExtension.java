package net.wizardsoflua.lua.extension.util;

import net.sandius.rembulan.Table;
import net.wizardsoflua.common.Named;
import net.wizardsoflua.lua.extension.api.inject.AfterInjection;
import net.wizardsoflua.lua.extension.api.inject.Resource;
import net.wizardsoflua.lua.extension.spi.SpellExtension;

public abstract class LuaTableExtension implements SpellExtension, Named {
  @Resource
  private Table env;

  @AfterInjection
  public void installIntoEnv() {
    String name = getName();
    Table table = getTable();
    env.rawset(name, table);
  }

  protected abstract Table getTable();
}
