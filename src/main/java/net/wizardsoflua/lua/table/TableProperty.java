package net.wizardsoflua.lua.table;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.sandius.rembulan.runtime.IllegalOperationAttemptException;

class TableProperty<T> {
  private final @Nullable Supplier<T> get;
  private final @Nullable Consumer<Object> set;

  public TableProperty(@Nullable Supplier<T> get, @Nullable Consumer<Object> set) {
    checkArgument(get != null || set != null, "Property must be writeable or readable");
    this.get = get;
    this.set = set;
  }

  public T get() {
    if (get == null)
      throw new IllegalOperationAttemptException("attempt to access write-only table index");
    return get.get();
  }

  public void set(Object value) {
    if (set == null)
      throw new IllegalOperationAttemptException("attempt to modify read-only table index");
    set.accept(value);
  }
}
