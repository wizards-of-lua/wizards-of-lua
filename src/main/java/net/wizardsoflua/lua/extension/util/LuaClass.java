package net.wizardsoflua.lua.extension.util;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;
import net.wizardsoflua.common.Named;
import net.wizardsoflua.extension.api.inject.PostConstruct;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.SpellExtensions;
import net.wizardsoflua.lua.module.types.TypesModule;

public abstract class LuaClass<J, L> extends TypeTokenLuaConverter<J, L> implements Named {
  @Resource
  private SpellExtensions extensions;
  @Resource
  private Table env;

  private @Nullable Table table;

  @PostConstruct
  public void registerClass() {
    TypesModule types = extensions.getSpellExtension(TypesModule.class);
    types.registerClass(getName(), getTable());
  }

  @PostConstruct
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
