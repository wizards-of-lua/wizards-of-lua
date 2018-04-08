package net.wizardsoflua.lua.extension.spi;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.extension.api.InitializationContext;

public interface LuaExtension {
  void initialize(InitializationContext context);

  void installInto(Table env);
}
