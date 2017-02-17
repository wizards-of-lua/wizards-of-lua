package net.karneim.luamod.cache;

import java.util.HashMap;
import java.util.Map;

import net.sandius.rembulan.runtime.LuaFunction;

public class LuaFunctionCache {

  private final Map<String, LuaFunction> map = new HashMap<>();

  public LuaFunction get(String key) {
    return map.get(key);
  }

  public void put(String key, LuaFunction fn) {
    map.put(key, fn);
  }
  
  public void clear() {
    map.clear();
  }

}
