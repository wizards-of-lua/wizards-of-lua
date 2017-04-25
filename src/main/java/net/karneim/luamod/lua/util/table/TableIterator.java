package net.karneim.luamod.lua.util.table;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.sandius.rembulan.Table;

public class TableIterator implements Iterator<Entry<Object, Object>> {
  private final Table table;
  private @Nullable Object nextKey;

  public TableIterator(Table table) {
    this.table = checkNotNull(table, "table == null!");
    this.nextKey = table.initialKey();
  }

  @Override
  public boolean hasNext() {
    return nextKey != null;
  }

  @Override
  public Entry<Object, Object> next() {
    Object key = nextKey;
    Object value = table.rawget(key);
    nextKey = table.successorKeyOf(nextKey);
    return Maps.immutableEntry(key, value);
  }
}
