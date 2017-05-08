package net.karneim.luamod.lua.util.table;

public interface MissingPropertyHandler {
  Object getMissingProperty(Object key);

  void setMissingProperty(Object key, Object value);
}
