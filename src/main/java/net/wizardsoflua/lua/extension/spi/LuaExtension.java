package net.wizardsoflua.lua.extension.spi;

import net.sandius.rembulan.Table;

public interface LuaExtension {
  void installInto(Table env);
}
