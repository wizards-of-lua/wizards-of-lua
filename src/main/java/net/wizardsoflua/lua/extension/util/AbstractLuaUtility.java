package net.wizardsoflua.lua.extension.util;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.extension.spi.LuaExtension;

public interface AbstractLuaUtility extends LuaExtension {
  @Override
  default void installInto(Table env) {
    String name = getName();
    Object luaObject = getLuaObject();
    env.rawset(name, luaObject);
  }

  String getName();

  Object getLuaObject();
}
