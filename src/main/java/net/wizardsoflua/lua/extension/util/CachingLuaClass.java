package net.wizardsoflua.lua.extension.util;

import java.util.concurrent.ConcurrentMap;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public abstract class CachingLuaClass<J, L> extends LuaClass<J, L> {
  private final Cache<J, L> cache = CacheBuilder.newBuilder().weakKeys().softValues().build();

  public Cache<J, L> getCache() {
    return cache;
  }

  @Override
  public L getLuaInstance(J javaInstance) {
    Cache<J, L> cache = getCache();
    ConcurrentMap<J, L> map = cache.asMap();
    return map.computeIfAbsent(javaInstance, this::toLuaInstance);
  }

  protected abstract L toLuaInstance(J javaInstance);
}
