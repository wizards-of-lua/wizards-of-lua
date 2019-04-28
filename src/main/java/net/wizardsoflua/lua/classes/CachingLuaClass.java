package net.wizardsoflua.lua.classes;

import java.util.concurrent.ConcurrentMap;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.wizardsoflua.extension.spell.spi.LuaConverter;

public abstract class CachingLuaClass<J, L> extends AnnotatedLuaClass
    implements LuaConverter<J, L> {
  private final Cache<J, L> cache = CacheBuilder.newBuilder().weakKeys().softValues().build();

  protected Cache<J, L> getCache() {
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
