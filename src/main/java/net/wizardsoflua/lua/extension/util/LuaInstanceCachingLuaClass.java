package net.wizardsoflua.lua.extension.util;

import java.util.concurrent.ConcurrentMap;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public abstract class LuaInstanceCachingLuaClass<J, L> extends LuaClass<J, L> {
  private final Cache<J, L> cache = CacheBuilder.newBuilder().weakKeys().softValues().build();

  public Cache<J, L> getCache() {
    return cache;
  }

  @Override
  public L getLuaInstance(J javaInstance) {
    Cache<J, L> cache = getCache();
    ConcurrentMap<J, L> map = cache.asMap();
    return map.computeIfAbsent(javaInstance, this::toLua);
  }

  protected abstract L toLua(J javaInstance);

  @Override
  public J getJavaInstance(L luaInstance) {
    return toJava(luaInstance);
  }

  protected abstract J toJava(L luaInstance);
}
