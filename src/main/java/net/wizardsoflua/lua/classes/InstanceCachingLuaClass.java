package net.wizardsoflua.lua.classes;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.sandius.rembulan.Table;

public abstract class InstanceCachingLuaClass<J> extends LuaClass<J> {
  private final Cache<J, Table> cache = CacheBuilder.newBuilder().weakKeys().softValues().build();

  public InstanceCachingLuaClass(Class<J> type) {
    super(type);
  }

  public Cache<J, Table> getCache() {
    return cache;
  }

  @Override
  public final Table getLuaInstance(J delegate) {
    return getCache().asMap().computeIfAbsent(delegate, super::getLuaInstance);
  }
}
