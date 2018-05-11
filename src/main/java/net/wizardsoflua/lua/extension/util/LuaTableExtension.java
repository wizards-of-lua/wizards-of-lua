package net.wizardsoflua.lua.extension.util;

import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.api.Named;
import net.wizardsoflua.extension.api.inject.PostConstruct;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.spi.SpellExtension;

public abstract class LuaTableExtension implements SpellExtension, Named {
  @Resource
  private Table env;

  @PostConstruct
  protected void installIntoEnv() {
    String name = getName();
    Table table = getTable();
    env.rawset(name, table);
  }

  protected abstract Table getTable();
}
