package net.wizardsoflua.lua.classes;

import com.google.auto.service.AutoService;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.TableFactory;
import net.wizardsoflua.lua.extension.api.inject.AfterInjection;
import net.wizardsoflua.lua.extension.api.inject.Inject;
import net.wizardsoflua.lua.extension.spi.SpellExtension;
import net.wizardsoflua.lua.extension.util.LuaTableExtension;

@AutoService(SpellExtension.class)
public class ObjectClass2 extends LuaTableExtension {
  @Inject
  private TableFactory tableFactory;

  private Table table;

  @AfterInjection
  public void init() {
    table = tableFactory.newTable();
  }

  @Override
  public String getName() {
    return "Object";
  }

  @Override
  public Table getTable() {
    return table;
  }
}
