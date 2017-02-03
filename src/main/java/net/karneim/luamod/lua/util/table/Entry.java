package net.karneim.luamod.lua.util.table;

import javax.annotation.Nullable;

public class Entry<K, V> {
  private K key;
  private V value;

  public Entry(@Nullable K key, @Nullable V value) {
    this.key = key;
    this.value = value;
  }

  public @Nullable K getKey() {
    return key;
  }

  public @Nullable V getValue() {
    return value;
  }
}
