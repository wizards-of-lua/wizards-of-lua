package net.wizardsoflua.extension;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class ClassIndex {
  private final Map<Class<?>, Object> map = new LinkedHashMap<>();

  public void add(Object element) {
    Class<?> cls = element.getClass();
    map.put(cls, element);
  }

  public @Nullable <E> E get(Class<E> cls) {
    Object value = map.get(cls);
    return cls.cast(value);
  }

  public Collection<Object> values() {
    return Collections.unmodifiableCollection(map.values());
  }
}
