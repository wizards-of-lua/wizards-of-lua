package net.karneim.luamod.lua;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.util.TraversableHashMap;

public class DynamicTable extends Table {

  private final Object model;
  private final Map<Object, Entry> entries;
  private final Object initialKey; // null iff the table is empty

  static class Entry {

    private final Object value;
    private final Object nextKey; // may be null

    private Entry(Object value, Object nextKey) {
      this.value = Objects.requireNonNull(value);
      this.nextKey = nextKey;
    }

  }

  DynamicTable(Object model, Map<Object, Entry> entries, Object initialKey) {
    this.model = model;
    this.entries = Objects.requireNonNull(entries);
    this.initialKey = initialKey;
  }

  public Object getModel() {
    return model;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    DynamicTable that = (DynamicTable) o;
    // Patch (mk, 2017-01-20): Prevent NPE on initialKey
    if (that.entries.isEmpty() && this.entries.isEmpty()) {
      return true;
    }
    return this.entries.equals(that.entries) && this.initialKey.equals(that.initialKey);
  }

  @Override
  public int hashCode() {
    // Patch (mk, 2017-01-20): Prevent NPE on initialKey
    if (entries.isEmpty()) {
      return 0;
    }
    int result = entries.hashCode();
    result = 31 * result + initialKey.hashCode();
    return result;
  }

  @Override
  public Object rawget(Object key) {
    key = Conversions.normaliseKey(key);
    Entry e = entries.get(key);
    return e != null ? e.value : null;
  }

  /**
   * Throws an {@link UnsupportedOperationException}, since this table is immutable.
   *
   * @param key ignored
   * @param value ignored
   *
   * @throws UnsupportedOperationException every time this method is called
   */
  @Override
  public void rawset(Object key, Object value) {
    throw new UnsupportedOperationException("table is immutable");
  }

  /**
   * Throws an {@link UnsupportedOperationException}, since this table is immutable.
   *
   * @param idx ignored
   * @param value ignored
   *
   * @throws UnsupportedOperationException every time this method is called
   */
  @Override
  public void rawset(long idx, Object value) {
    throw new UnsupportedOperationException("table is immutable");
  }

  @Override
  public Table getMetatable() {
    return null;
  }

  /**
   * Throws an {@link UnsupportedOperationException}, since this table is immutable.
   *
   * @param mt ignored
   * @return nothing (always throws an exception)
   *
   * @throws UnsupportedOperationException every time this method is called
   */
  @Override
  public Table setMetatable(Table mt) {
    throw new UnsupportedOperationException("table is immutable");
  }

  @Override
  public Object initialKey() {
    return initialKey;
  }

  @Override
  public Object successorKeyOf(Object key) {
    key = Conversions.normaliseKey(key);
    try {
      Entry e = entries.get(key);
      return e.nextKey;
    } catch (NullPointerException ex) {
      throw new IllegalArgumentException("invalid key to 'next'", ex);
    }
  }

  @Override
  protected void setMode(boolean weakKeys, boolean weakValues) {
    // no-op
  }

  /**
   * Builder class for constructing instances of {@link DynamicTable}.
   */
  public static class Builder {

    private final Object model;
    private final TraversableHashMap<Object, Object> entries;

    private static void checkKey(Object key) {
      if (key == null || (key instanceof Double && Double.isNaN(((Double) key).doubleValue()))) {
        throw new IllegalArgumentException(
            "invalid table key: " + Conversions.toHumanReadableString(key));
      }
    }

    private Builder(Object model, TraversableHashMap<Object, Object> entries) {
      this.model = model;
      this.entries = Objects.requireNonNull(entries);
    }

    /**
     * Constructs a new empty builder.
     */
    public Builder(Object model) {
      this(model, new TraversableHashMap<>());
    }

    private static <K, V> TraversableHashMap<K, V> mapCopy(TraversableHashMap<K, V> map) {
      TraversableHashMap<K, V> result = new TraversableHashMap<>();
      result.putAll(map);
      return result;
    }

    /**
     * Sets the value associated with the key {@code key} to {@code value}.
     *
     * <p>
     * The behaviour of this method is similar to that of {@link Table#rawset(Object, Object)}:
     * </p>
     * <ul>
     * <li>when {@code value} is <b>nil</b> (i.e., {@code null}), the key {@code key} will not have
     * any value associated with it after this method returns;</li>
     * <li><b>nil</b> and <i>NaN</i> keys are rejected by throwing a
     * {@link IllegalArgumentException};</li>
     * <li>numeric keys with an integer value are converted to that integer value.</li>
     * </ul>
     *
     * <p>
     * The method returns {@code this}, allowing calls to this method to be chained.
     * </p>
     *
     * @param key the key, must not be {@code null} or <i>NaN</i>
     * @param value the value, may be {@code null}
     * @return this builder
     *
     * @throws IllegalArgumentException when {@code key} is {@code null} or a <i>NaN</i>
     */
    public Builder add(Object key, Object value) {
      key = Conversions.normaliseKey(key);
      checkKey(key);

      if (value != null) {
        entries.put(key, value);
      } else {
        entries.remove(key);
      }

      return this;
    }

    /**
     * Clears the builder.
     */
    public void clear() {
      entries.clear();
    }

    /**
     * Constructs and returns a new immutable table based on the contents of this builder.
     *
     * @return a new immutable table
     */
    public DynamicTable build() {
      Map<Object, Entry> tableEntries = new HashMap<>();

      for (Map.Entry<Object, Object> e : entries.entrySet()) {
        Object k = e.getKey();
        tableEntries.put(e.getKey(), new Entry(e.getValue(), entries.getSuccessorOf(k)));
      }
      return new DynamicTable(model, Collections.unmodifiableMap(tableEntries),
          entries.getFirstKey());
    }

  }

}
