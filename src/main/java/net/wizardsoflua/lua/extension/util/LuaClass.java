package net.wizardsoflua.lua.extension.util;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.api.Named;
import net.wizardsoflua.extension.api.inject.PostConstruct;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaTypes;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.classes.ObjectClass2;

public abstract class LuaClass implements SpellExtension, Named {
  @Resource
  private Table env;
  @Resource
  private Injector injector;
  @Resource
  private LuaTypes types;

  private @Nullable Table table;

  @PostConstruct
  protected void registerClass() {
    types.registerLuaClass(getName(), getTable());
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
      Table metatable = getMetatable();
      table.setMetatable(metatable);
    }
    return table;
  }

  protected abstract Table createRawTable();

  protected @Nullable Table getMetatable() {
    Class<? extends LuaClass> superClassClass = getSuperClassClass();
    LuaClass superClass = injector.getInstance(superClassClass);
    return superClass.getTable();
  }

  protected Class<? extends LuaClass> getSuperClassClass() {
    return ObjectClass2.class;
  }
}
