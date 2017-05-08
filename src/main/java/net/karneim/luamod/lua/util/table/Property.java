package net.karneim.luamod.lua.util.table;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public class Property<T> {
  private final @Nullable Supplier<T> get;
  private final @Nullable Consumer<Object> set;

  public Property(@Nullable Supplier<T> get, @Nullable Consumer<Object> set) {
    if (get == null && set == null) {
      throw new IllegalArgumentException("Property must either be writeable or readable");
    }
    this.get = get;
    this.set = set;
  }

  public T get() {
    if (get == null)
      throw new UnsupportedOperationException("property is writeonly");
    return get.get();
  }

  public void set(Object value) {
    if (set == null)
      throw new UnsupportedOperationException("property is readonly");
    set.accept(value);
  }
}
