package net.wizardsoflua.lua.extension.util;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;
import net.wizardsoflua.common.Named;
import net.wizardsoflua.lua.extension.api.inject.AfterInjection;
import net.wizardsoflua.lua.extension.api.inject.Resource;
import net.wizardsoflua.lua.extension.api.service.SpellExtensions;
import net.wizardsoflua.lua.module.types.TypesModule;

public abstract class LuaClass<J, L> extends TypeTokenLuaConverter<J, L> implements Named {
  @Resource
  private Table env;
  @Resource
  private SpellExtensions extensions;

  private @Nullable Table table;

  @AfterInjection
  public void init() {
    TypesModule types = extensions.getSpellExtension(TypesModule.class);
    types.registerClass(getName(), getTable());
  }

  @AfterInjection
  public void installIntoEnv() {
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
