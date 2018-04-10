package net.wizardsoflua.lua.extension.spi;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.extension.util.SpellExtension;

public interface LuaExtension extends SpellExtension {
  void installInto(Table env);
}
