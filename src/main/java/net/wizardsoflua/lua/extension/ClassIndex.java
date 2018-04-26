package net.wizardsoflua.lua.extension;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class ClassIndex {
  private final Map<Class<?>, Object> map = new HashMap<>();

  public void add(Object element) {
    Class<?> cls = element.getClass();
    map.put(cls, element);
  }

  public @Nullable <E> E get(Class<E> cls) {
    Object value = map.get(cls);
    return cls.cast(value);
  }
}
