package net.wizardsoflua.lua.classes;

import javax.annotation.Nullable;
import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.api.inject.PostConstruct;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaTypes;
import net.wizardsoflua.lua.extension.LuaTableExtension;

public abstract class LuaClass extends LuaTableExtension {
  @Resource
  private Injector injector;
  @Resource
  private LuaTypes types;

  private @Nullable Table table;

  @PostConstruct
  protected void registerClass() {
    types.registerLuaClass(getName(), getTable());
  }

  @Override
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
    return ObjectClass.class;
  }
}
