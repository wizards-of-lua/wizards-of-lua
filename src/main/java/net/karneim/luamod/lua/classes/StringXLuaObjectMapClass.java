package net.karneim.luamod.lua.classes;

import java.util.Map;

import net.karneim.luamod.lua.wrapper.StringXLuaObjectMapInstance;
import net.sandius.rembulan.Table;

public class StringXLuaObjectMapClass {
  private static final StringXLuaObjectMapClass SINGLETON = new StringXLuaObjectMapClass();

  public static StringXLuaObjectMapClass get() {
    return SINGLETON;
  }

  public StringXLuaObjectMapInstance newInstance(Table env, Map<String, Object> delegate) {
    return new StringXLuaObjectMapInstance(env, delegate, null);
  }

}
