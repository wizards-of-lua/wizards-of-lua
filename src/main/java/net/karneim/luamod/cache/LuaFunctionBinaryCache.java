package net.karneim.luamod.cache;

import java.util.HashMap;
import java.util.Map;

import net.karneim.luamod.lua.patched.LuaFunctionBinary;

public class LuaFunctionBinaryCache {

  private final Map<String, LuaFunctionBinary> map = new HashMap<>();

  public LuaFunctionBinary get(String key) {
    return map.get(key);
  }

  public void put(String key, LuaFunctionBinary fn) {
    map.put(key, fn);
  }
  
  public void clear() {
    map.clear();
  }

}
