package net.wizardsoflua.lua.extension.spi;

import net.sandius.rembulan.Table;

public interface LuaExtension extends SpellExtension {
  void installInto(Table env);
}
