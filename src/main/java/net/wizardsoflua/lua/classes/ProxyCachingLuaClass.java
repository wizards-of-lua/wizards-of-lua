package net.wizardsoflua.lua.classes;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.wizardsoflua.lua.classes.common.LuaInstanceProxy;

public abstract class ProxyCachingLuaClass<J, P extends LuaInstanceProxy<? extends J>>
    extends ProxyingLuaClass<J, P> {
  private final Cache<J, P> cache = CacheBuilder.newBuilder().weakKeys().softValues().build();

  @Override
  public final P getLuaInstance(J delegate) {
    return cache.asMap().computeIfAbsent(delegate, super::getLuaInstance);
  }

  public Cache<J, P> getCache() {
    return cache;
  }
}
