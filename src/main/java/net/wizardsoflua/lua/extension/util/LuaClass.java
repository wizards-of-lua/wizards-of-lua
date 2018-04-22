package net.wizardsoflua.lua.extension.util;

import javax.annotation.Nullable;
import javax.inject.Inject;

import net.sandius.rembulan.Table;
import net.wizardsoflua.common.Named;
import net.wizardsoflua.extension.api.inject.PostConstruct;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.lua.module.types.TypesModule;

public abstract class LuaClass<J, L> extends TypeTokenLuaConverter<J, L> implements Named {
  @Resource
  private Table env;
  @Inject
  private TypesModule types;

  private @Nullable Table table;

  @PostConstruct
  protected void registerClass() {
    types.registerClass(getName(), getTable());
  }

  @PostConstruct
  protected void installIntoEnv() {
    String name = getName();
    Table table = getTable();
    env.rawset(name, table);
  }

  public Table getTable() {
    if (table == null) {
      table = createRawTable();
      table.rawset("__index", table);
    }
    return table;
  }

  protected abstract Table createRawTable();
}
