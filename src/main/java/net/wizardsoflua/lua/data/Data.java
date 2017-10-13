package net.wizardsoflua.lua.data;

import java.util.IdentityHashMap;
import java.util.Map.Entry;
import java.util.Optional;

import javax.annotation.Nullable;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.table.TableIterable;

public class Data {

  public static Data createData(Object luaObj, Converters converters) {
    return new Data(copyObj(luaObj, new IdentityHashMap<>(), converters));
  }

  private final @Nullable Object content;

  private Data(Object content) {
    this.content = content;
  }

  public @Nullable Object getContent() {
    return content;
  }

  private static Object copyObj(Object luaObj, IdentityHashMap<Object, Object> copies,
      Converters converters) {
    if (luaObj == null) {
      return null;
    }
    if (luaObj instanceof String //
        || luaObj instanceof Number//
        || luaObj instanceof Boolean//
    ) {
      return luaObj;
    }
    if (luaObj instanceof ByteString) {
      return converters.toJava(String.class, luaObj);
    }
    if (luaObj instanceof DelegatingProxy) {
      String classname = converters.getTypes().getClassname((Table) luaObj);
      if (classname != null) {
        return ((DelegatingProxy) luaObj).getDelegate();
      } else {
        throw new IllegalArgumentException(String.format(
            "Can't copy Lua object. Unsupported data type: %s", luaObj.getClass().getName()));
      }
    }
    if (luaObj instanceof Table) {
      Table table = (Table) luaObj;
      TableData result = copyTable(table, copies, converters);
      return result;
    }
    throw new IllegalArgumentException(
        String.format("Can't copy Lua object. Unsupported data type: %s", Optional
            .of(converters.getTypes().getTypename(luaObj)).orElse(luaObj.getClass().getName())));
  }

  private static TableData copyTable(Table table, IdentityHashMap<Object, Object> copies,
      Converters converters) {
    if (table == null) {
      return null;
    }
    Object copy = copies.get(table);
    if (copy != null) {
      TableData result = (TableData) copy;
      return result;
    }

    String typename = converters.getTypes().getClassname(table);
    IdentityHashMap<Object, Object> contents = new IdentityHashMap<>();
    TableData result = new TableData(contents, typename);
    copies.put(table, result);

    for (Entry<Object, Object> entry : new TableIterable(table)) {
      Object newKey = copyObj(entry.getKey(), copies, converters);
      Object newValue = copyObj(entry.getValue(), copies, converters);
      contents.put(newKey, newValue);
    }
    return result;
  }


}
