package net.wizardsoflua.lua.table;

import java.util.Objects;

import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.util.TraversableHashMap;

/**
 * A readonly table.
 * 
 */
public class ReadonlyTable extends Table {

  private final TraversableHashMap<Object, Object> entries;

  private ReadonlyTable(TraversableHashMap<Object, Object> entries) {
    this.entries = Objects.requireNonNull(entries);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ReadonlyTable that = (ReadonlyTable) o;
    if (entries.isEmpty()) {
      return that.entries.isEmpty();
    } else {
      return this.entries.equals(that.entries);
    }
  }

  @Override
  public int hashCode() {
    if (entries.isEmpty()) {
      return 0;
    }
    int result = entries.hashCode();
    return result;
  }

  @Override
  public Object rawget(Object key) {
    key = Conversions.normaliseKey(key);
    return entries.get(key);
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
    throw new UnsupportedOperationException("table is readonly");
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
    throw new UnsupportedOperationException("table is readonly");
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
    throw new UnsupportedOperationException("table is readonly");
  }

  Table superSetMetatable(Table mt) {
    return super.setMetatable(mt);
  }

  @Override
  public Object initialKey() {
    if (entries.isEmpty()) {
      return null;
    }
    return entries.get(entries.getFirstKey());
  }

  @Override
  public Object successorKeyOf(Object key) {
    key = Conversions.normaliseKey(key);
    try {
      return entries.getSuccessorOf(key);
    } catch (NullPointerException ex) {
      throw new IllegalArgumentException("invalid key to 'next'", ex);
    }
  }

  @Override
  protected void setMode(boolean weakKeys, boolean weakValues) {
    // no-op
  }

  @Override
  public String toString() {
    return entries.toString();
  }

  public static class Accessor {

    private final TraversableHashMap<Object, Object> entries;
    private final ReadonlyTable table;

    private Accessor(TraversableHashMap<Object, Object> entries) {
      this.entries = Objects.requireNonNull(entries);
      this.table = new ReadonlyTable(entries);
    }

    /**
     * Constructs a new empty builder.
     */
    public Accessor() {
      this(new TraversableHashMap<>());
    }

    public Table getTable() {
      return table;
    }

    public Accessor add(Object key, Object value) {
      key = Conversions.normaliseKey(key);
      checkKey(key);

      if (value != null) {
        value = Conversions.canonicalRepresentationOf(value);
        entries.put(key, value);
      } else {
        entries.remove(key);
      }

      return this;
    }

    public Accessor setMetatable(Table mt) {
      this.table.superSetMetatable(mt);
      return this;
    }

    private static void checkKey(Object key) {
      if (key == null || (key instanceof Double && Double.isNaN(((Double) key).doubleValue()))) {
        throw new IllegalArgumentException(
            "invalid table key: " + Conversions.toHumanReadableString(key));
      }
    }
  }


}
