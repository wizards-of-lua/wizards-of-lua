package net.wizardsoflua.lua.extension.spi;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.extension.api.InitializationContext;

public interface LuaModule {
  void initialize(InitializationContext context);

  String getName();

  Table getTable();

  default void installInto(Table env) {
    String name = getName();
    Table table = getTable();
    env.rawset(name, table);
  }
}
