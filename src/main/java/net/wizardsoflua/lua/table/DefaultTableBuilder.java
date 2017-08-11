package net.wizardsoflua.lua.table;

import java.util.Map;
import java.util.Objects;

import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.util.TraversableHashMap;

public class DefaultTableBuilder {

  private final TraversableHashMap<Object, Object> entries;

  private Table metatable = null;

  private static void checkKey(Object key) {
    if (key == null || (key instanceof Double && Double.isNaN(((Double) key).doubleValue()))) {
      throw new IllegalArgumentException(
          "invalid table key: " + Conversions.toHumanReadableString(key));
    }
  }

  private DefaultTableBuilder(TraversableHashMap<Object, Object> entries) {
    this.entries = Objects.requireNonNull(entries);
  }

  /**
   * Constructs a new empty builder.
   */
  public DefaultTableBuilder() {
    this(new TraversableHashMap<>());
  }

  private static <K, V> TraversableHashMap<K, V> mapCopy(TraversableHashMap<K, V> map) {
    TraversableHashMap<K, V> result = new TraversableHashMap<>();
    result.putAll(map);
    return result;
  }

  /**
   * Constructs a copy of the given builder (a copy constructor).
   *
   * @param builder the original builder, must not be {@code null}
   *
   * @throws NullPointerException if {@code builder} is {@code null}
   */
  public DefaultTableBuilder(DefaultTableBuilder builder) {
    this(mapCopy(builder.entries));
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
   * <li><b>nil</b> and <i>NaN</i> keys are rejected by throwing a {@link IllegalArgumentException};
   * </li>
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
  public DefaultTableBuilder add(Object key, Object value) {
    key = Conversions.normaliseKey(key);
    checkKey(key);

    if (value != null) {
      entries.put(key, value);
    } else {
      entries.remove(key);
    }

    return this;
  }

  public DefaultTableBuilder setMetatable(Table table) {
    metatable = table;
    return this;
  }

  /**
   * Clears the builder.
   */
  public void clear() {
    entries.clear();
  }

  /**
   * Constructs and returns a new table based on the contents of this builder.
   *
   * @return a new table
   */
  public DefaultTable build() {
    DefaultTable result = new DefaultTable();
    for (Map.Entry<Object, Object> e : entries.entrySet()) {
      Object k = e.getKey();
      result.rawset(k, e.getValue());
    }
    result.setMetatable(metatable);
    return result;
  }

}
