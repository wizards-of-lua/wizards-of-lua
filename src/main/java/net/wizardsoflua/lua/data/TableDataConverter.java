package net.wizardsoflua.lua.data;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.IdentityHashMap;
import java.util.Map;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.common.WeakKeySoftValueMap;

public class TableDataConverter {
  private final WeakKeySoftValueMap<Table> cache = new WeakKeySoftValueMap<>();
  private final Converters converters;

  public TableDataConverter(Converters converters) {
    this.converters = checkNotNull(converters, "converters==null!");;
  }

  public Table toLua(TableData data) {
    if (data == null) {
      return null;
    }
    IdentityHashMap<Object, Object> map = data.getContents();
    Table result = cache.get(map);
    if (result != null) {
      return result;
    }
    result = DefaultTable.factory().newTable();
    cache.put(map, result);
    for (Map.Entry<Object, Object> e : map.entrySet()) {
      Object javaKey = e.getKey();
      Object luaKey = converters.toLua(javaKey);
      Object javaValue = e.getValue();
      Object luaValue = converters.toLuaNullable(javaValue);
      result.rawset(luaKey, luaValue);
    }
    Table mt = converters.getTypes().getClassMetatable(data.getClassname());
    if (mt != null) {
      result.setMetatable(mt);
    }
    return result;
  }

}
