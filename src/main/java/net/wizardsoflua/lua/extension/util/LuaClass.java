package net.wizardsoflua.lua.extension.util;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.extension.api.inject.AfterInjection;
import net.wizardsoflua.lua.extension.api.inject.Inject;
import net.wizardsoflua.lua.extension.api.service.LuaExtensionLoader;
import net.wizardsoflua.lua.module.types.TypesModule;

public abstract class LuaClass<J, L> extends TypeTokenConverterExtension<J, L>
    implements LuaTableExtension {
  @Inject
  private LuaExtensionLoader extensionLoader;

  private @Nullable Table table;

  @AfterInjection
  public void init() {
    TypesModule types = extensionLoader.getLuaExtension(TypesModule.class);
    types.registerClass(getName(), getTable());
  }

  @Override
  public Table getTable() {
    if (table == null) {
      table = createRawTable();
      table.rawset("__index", table);
    }
    return table;
  }

  protected abstract Table createRawTable();
}
