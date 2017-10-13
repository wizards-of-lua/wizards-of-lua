package net.wizardsoflua.lua.classes.common;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.MapMaker;

public class WeakKeySoftValueMap<V> {
  private final Map<Object, SoftReference<V>> contents = new MapMaker().weakKeys().makeMap();

  public V computeIfAbsent(Object key, Supplier<V> supplier) {
    SoftReference<V> valueRef = contents.get(key);
    if (valueRef == null || valueRef.get() == null) {
      valueRef = soft(supplier.get());
      contents.put(key, valueRef);
    }
    return valueRef.get();
  }
  
  public V get(Object key) {
    SoftReference<V> valueRef = contents.get(key);
    if (valueRef != null) {
      return valueRef.get();
    }
    return null;
  }
  
  public void put(Object key, V value) {
    SoftReference<V> valueRef = soft(value);
    contents.put(key, valueRef);
  }

  private <T> SoftReference<T> soft(T value) {
    return new SoftReference<T>(value);
  }
}
