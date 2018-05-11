package net.wizardsoflua.lua.table;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.util.TraversableHashMap;
import net.wizardsoflua.extension.api.Named;

/**
 * A {@link Table} implementation that can be used as a Lua interface for Java properties.
 *
 * @author Adrodoc
 */
public class PropertyTable extends Table {
  private final TraversableHashMap<Object, Object> values = new TraversableHashMap<>();
  /**
   * Whether or not this table should be modifiable from Lua. If this is {@code true} then Lua code
   * will be able to add additional functions and properties and change values that are not wrapped
   * in a {@link TableProperty}.
   */
  private final boolean modifiable;

  @Deprecated
  public PropertyTable() {
    this(false);
  }

  public PropertyTable(boolean modifiable) {
    this.modifiable = modifiable;
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
    Object result = values.get(key);
    if (result instanceof TableProperty<?>) {
      result = ((TableProperty<?>) result).get();
    }
    return Conversions.canonicalRepresentationOf(result);
  }

  @Override
  public void rawset(Object key, Object value) {
    key = Conversions.normaliseKey(key);
    checkKey(key);
    value = Conversions.canonicalRepresentationOf(value);
    Object p = values.get(key);
    if (p instanceof TableProperty<?>) {
      ((TableProperty<?>) p).set(value);
    } else {
      if (modifiable) {
        values.put(key, value);
      } else {
        throw new IllegalOperationAttemptException("attempt to modify unknown table index");
      }
    }
    updateBasetableModes(key, value);
  }

  @Override
  public Object initialKey() {
    return values.getFirstKey();
  }

  @Override
  public Object successorKeyOf(Object key) {
    try {
      return values.getSuccessorOf(key);
    } catch (NoSuchElementException | NullPointerException ex) {
      throw new IllegalArgumentException("invalid key to 'next'", ex);
    }
  }

  @Override
  protected void setMode(boolean weakKeys, boolean weakValues) {
    // no-op
  }

  public <T> void addReadOnly(Object key, Supplier<T> get) {
    addProperty(key, new TableProperty<>(get, null));
  }

  public <T> void add(Object key, @Nullable Supplier<T> get, Consumer<Object> set) {
    checkNotNull(set, "set == null!");
    addProperty(key, new TableProperty<>(get, set));
  }

  private void addProperty(Object key, TableProperty<?> property) {
    add(key, property);
  }

  public <F extends LuaFunction & Named> void addFunction(F function) {
    add(function.getName(), function);
  }

  public void addFunction(Object key, LuaFunction function) {
    add(key, function);
  }

  private void add(Object key, Object value) {
    key = Conversions.normaliseKey(key);
    checkKey(key);
    checkNotNull(value, "value == null!");
    values.put(key, value);
  }

  /**
   * @deprecated <a href= "https://github.com/wizards-of-lua/wizards-of-lua/issues/29">#29</a>
   */
  @Deprecated
  public void addImmutable(Object key, Object value) {
    TableProperty<?> property = new TableProperty<>(() -> value, null);
    addProperty(key, property);
  }

  /**
   * @deprecated <a href= "https://github.com/wizards-of-lua/wizards-of-lua/issues/29">#29</a>
   */
  @Deprecated
  public void addImmutableNullable(Object key, @Nullable Object value) {
    if (value != null) {
      addImmutable(key, value);
    }
  }
}
