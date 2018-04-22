package net.wizardsoflua.lua.classes;

import com.google.auto.service.AutoService;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.TableFactory;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.extension.util.LuaTableExtension;

@AutoService(SpellExtension.class)
public class ObjectClass2 extends LuaTableExtension {
  private Table table;

  public void init(@Resource TableFactory tableFactory) {
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
