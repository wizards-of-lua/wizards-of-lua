package net.wizardsoflua.lua.classes;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.classes.common.WeakKeySoftValueMap;

public abstract class InstanceCachingLuaClass<J> extends LuaClass<J> {

  public static class Cache extends WeakKeySoftValueMap<Table> {
  }

  private final Cache cache = new Cache();

  public InstanceCachingLuaClass(Class<J> type) {
    super(type);
  }

  public Cache getCache() {
    return cache;
  }

  @Override
  public final Table getLuaInstance(J delegate) {
    return getCache().computeIfAbsent(delegate, () -> super.getLuaInstance(delegate));
  }

}
