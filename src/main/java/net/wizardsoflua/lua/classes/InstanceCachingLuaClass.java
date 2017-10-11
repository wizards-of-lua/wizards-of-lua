package net.wizardsoflua.lua.classes;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.MapMaker;

import net.sandius.rembulan.Table;

public abstract class InstanceCachingLuaClass<J> extends LuaClass<J> {

  public class Cache {
    private final Map<Object, SoftReference<Table>> content = new MapMaker().weakKeys().makeMap();

    public Table computeIfAbsent(Object key, Supplier<Table> supplier) {
      SoftReference<Table> valueRef = content.get(key);
      if (valueRef == null || valueRef.get() == null) {
        valueRef = soft(supplier.get());
        content.put(key, valueRef);
      }
      return valueRef.get();
    }

    private <T> SoftReference<T> soft(T value) {
      return new SoftReference<T>(value);
    }
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
