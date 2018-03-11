package net.wizardsoflua.lua.classes;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.wizardsoflua.lua.classes.common.LuaInstance;

public abstract class InstanceCachingLuaClass<J, I extends LuaInstance<? extends J>>
    extends ProxyingLuaClass<J, I> {
  private final Cache<J, I> cache = CacheBuilder.newBuilder().weakKeys().softValues().build();

  @Override
  public final I getLuaInstance(J delegate) {
    return cache.asMap().computeIfAbsent(delegate, super::getLuaInstance);
  }

  public Cache<J, I> getCache() {
    return cache;
  }
}
