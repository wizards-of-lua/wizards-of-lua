package net.karneim.luamod.lua.classes;

import java.util.Map;

import net.karneim.luamod.lua.wrapper.StringXLuaObjectMapInstance;

public class StringXLuaObjectMapClass {
  private static final StringXLuaObjectMapClass SINGLETON = new StringXLuaObjectMapClass();

  public static StringXLuaObjectMapClass get() {
    return SINGLETON;
  }

  public StringXLuaObjectMapInstance newInstance(LuaTypesRepo repo, Map<String, Object> delegate) {
    return new StringXLuaObjectMapInstance(repo, delegate, null);
  }

}
