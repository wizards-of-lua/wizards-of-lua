package net.wizardsoflua.lua.classes.common;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Metatables;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.util.TraversableHashMap;
import net.wizardsoflua.lua.table.Property;

public class DelegatingTable extends Table {
  private final TraversableHashMap<Object, Object> properties = new TraversableHashMap<>();

  public DelegatingTable() {
    this(null);
  }

  public DelegatingTable(Table metatable) {
    setMetatable(metatable);
  }

  private static void checkKey(Object key) {
    if (key == null) {
      throw new IllegalArgumentException("table index is nil");
    }
    if (key instanceof Double && Double.isNaN(((Double) key).doubleValue())) {
      throw new IllegalArgumentException("table index is NaN");
    }
  }

  @Override
  public Object rawget(Object key) {
    key = Conversions.normaliseKey(key);
    Object result = properties.get(key);
    if (result instanceof Property<?>) {
      result = ((Property<?>) result).get();
    }
    return result;
  }

  @Override
  public void rawset(Object key, Object value) {
    key = Conversions.normaliseKey(key);
    checkKey(key);
    value = Conversions.javaRepresentationOf(value);
    updateBasetableModes(key, value);
    if (Metatables.MT_MODE.equals(key)) {
      // TODO adrodoc55 22.01.2017: Muss der mode hier noch gespeichert werden?
      return;
    }

    Object p = properties.get(key);
    if (p == null)
      throw new IllegalArgumentException("unknown table index");
    if (p instanceof Property<?>) {
      ((Property<?>) p).set(value);
    } else {
      throw new UnsupportedOperationException("property is readonly");
    }
  }

  @Override
  public Object initialKey() {
    return properties.getFirstKey();
  }

  @Override
  public Object successorKeyOf(Object key) {
    try {
      return properties.getSuccessorOf(key);
    } catch (NoSuchElementException | NullPointerException ex) {
      throw new IllegalArgumentException("invalid key to 'next'", ex);
    }
  }

  @Override
  protected void setMode(boolean weakKeys, boolean weakValues) {
    // no-op
  }

  public <T> void addReadOnly(Object key, Supplier<T> get) {
    addImmutable(key, new Property<T>(get, null));
  }

  public <T> void add(Object key, @Nullable Supplier<T> get, Consumer<Object> set) {
    checkNotNull(set, "set == null!");
    addImmutable(key, new Property<T>(get, set));
  }

  public void addImmutable(Object key, Object value) {
    key = Conversions.normaliseKey(key);
    checkKey(key);

    checkNotNull(value, "value == null!");
    value = Conversions.canonicalRepresentationOf(value);
    properties.put(key, value);
  }

  public void addImmutableNullable(Object key, @Nullable Object value) {
    if (value != null) {
      addImmutable(key, value);
    }
  }

}
