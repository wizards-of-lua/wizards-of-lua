package net.wizardsoflua.lua.classes;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.classes.common.Delegator;

public abstract class InstanceCachingLuaClass<J, L extends Table & Delegator<? extends J>>
    extends DelegatorLuaClass<J, L> {
  private final Cache<J, L> cache = CacheBuilder.newBuilder().weakKeys().softValues().build();

  @Override
  public final L getLuaInstance(J delegate) {
    return cache.asMap().computeIfAbsent(delegate, this::toLua);
  }

  public Cache<J, L> getCache() {
    return cache;
  }
}
