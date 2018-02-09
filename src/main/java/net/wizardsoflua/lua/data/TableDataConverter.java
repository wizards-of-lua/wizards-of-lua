package net.wizardsoflua.lua.data;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.util.TraversableHashMap;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.LuaClass;
import net.wizardsoflua.lua.classes.LuaClassLoader;

public class TableDataConverter {
  private final Cache<TraversableHashMap<Object, Object>, Table> cache =
      CacheBuilder.newBuilder().weakKeys().softValues().build();
  private final LuaClassLoader classLoader;
  private final Converters converters;

  public TableDataConverter(LuaClassLoader classLoader) {
    this.classLoader = requireNonNull(classLoader, "classLoader == null!");
    converters = classLoader.getConverters();
  }

  public Table toLua(TableData data) {
    if (data == null) {
      return null;
    }
    TraversableHashMap<Object, Object> map = data.getContents();
    Table result = cache.getIfPresent(map);
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
    Table mt = getMetatable(data);
    if (mt != null) {
      result.setMetatable(mt);
    }
    return result;
  }

  private @Nullable Table getMetatable(TableData data) {
    String classname = data.getClassname();
    if (classname == null) {
      return null;
    }
    LuaClass luaClass = classLoader.getLuaClassForName(classname);
    if (luaClass == null) {
      return null;
    }
    return luaClass.getMetaTable();
  }
}
