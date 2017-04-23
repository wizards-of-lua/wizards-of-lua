package net.karneim.luamod.lua.classes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import net.sandius.rembulan.Table;

public class LuaTypesRepo {

  private final Map<String, LuaType> types = new HashMap<String, LuaType>();
  private final Table env;

  public LuaTypesRepo(Table env) {
    this.env = checkNotNull(env);
  }

  public Table getEnv() {
    return env;
  }

  public <T extends LuaType> void register(T luaType) {
    put(luaType.getTypeName(), luaType);
  }

  public <T extends LuaType> T get(Class<T> cls) {
    return get(LuaType.typeNameOf(cls));
  }

  private <T extends LuaType> void put(String name, T luaType) {
    if (types.containsKey(name)) {
      throw new IllegalArgumentException(String.format("Type %s is already definded!", luaType));
    }
    luaType.setRepo(this);
    types.put(name, luaType);
  }

  public boolean isRegistered(String name) {
    return types.containsKey(name);
  }

  public <T extends LuaType> T get(String name) {
    LuaType obj = types.get(name);
    return (T) obj;
  }
}
