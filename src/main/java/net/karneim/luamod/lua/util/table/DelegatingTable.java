package net.karneim.luamod.lua.util.table;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Metatables;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.util.TraversableHashMap;

public class DelegatingTable<O> extends Table {
  private final TraversableHashMap<Object, Object> properties = new TraversableHashMap<>();
  private final O delegate;

  protected DelegatingTable(O delegate, Map<?, ?> properties) {
    this.delegate = checkNotNull(delegate, "delegate == null!");
    this.properties.putAll(properties);
  }

  public O getDelegate() {
    return delegate;
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
      result = ((Property) result).get();
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
      ((Property) p).set(value);
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

  public static <O> Builder<O> builder(O delegate) {
    return new Builder<O>(delegate);
  }

  /**
   * Builder class for constructing instances of {@link DelegatingTable2}.
   */
  public static class Builder<O> {
    private final O delegate;
    private final Map<Object, Object> properties = new HashMap<>();

    /**
     * Constructs a new empty builder.
     */
    private Builder(O delegate) {
      this.delegate = checkNotNull(delegate, "delegate == null!");
    }

    public <T> Builder add(Object key, @Nullable Supplier<T> get, @Nullable Consumer<T> set) {
      return add(key, new Property(get, set));
    }

    public Builder add(Object key, Object value) {
      key = Conversions.normaliseKey(key);
      checkKey(key);

      checkNotNull(value, "value == null!");
      value = Conversions.canonicalRepresentationOf(value);
      properties.put(key, value);

      return this;
    }
    
    public Builder addNullable(Object key, @Nullable Object value) {
      if ( value != null) {
        add(key,value);
      }
      return this;
    }

    /**
     * Constructs and returns a new immutable table based on the contents of this builder.
     *
     * @return a new immutable table
     */
    public DelegatingTable build() {
      return new DelegatingTable<O>(delegate, properties);
    }
  }
}
