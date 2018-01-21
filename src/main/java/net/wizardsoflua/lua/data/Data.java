package net.wizardsoflua.lua.data;

import java.util.IdentityHashMap;
import java.util.Map.Entry;
import java.util.Optional;

import javax.annotation.Nullable;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.util.TraversableHashMap;
import net.wizardsoflua.event.CustomLuaEvent;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.classes.event.CustomEventClass;
import net.wizardsoflua.lua.table.TableIterable;

public class Data {
  public static Data empty() {
    return new Data(null);
  }

  public static Data createData(Object javaObj) {
    return new Data(javaObj);
  }

  public static Data createData(Object luaObj, Converters converters) {
    return new Data(transferObj(luaObj, new IdentityHashMap<>(), converters));
  }

  private final @Nullable Object content;

  private Data(@Nullable Object content) {
    this.content = content;
  }

  public @Nullable Object getContent() {
    return content;
  }

  private static Object transferObj(Object luaObj, IdentityHashMap<Object, Object> copies,
      Converters converters) {
    if (luaObj == null) {
      return null;
    }
    Object copy = copies.get(luaObj);
    if (copy != null) {
      return copy;
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
    if (luaObj instanceof CustomEventClass.Proxy) {
      CustomEventClass.Proxy<?> proxy = (CustomEventClass.Proxy<?>) luaObj;
      CustomLuaEvent delegate = proxy.getDelegate();
      Data data = Data.createData(proxy.rawget("data"), converters);
      // return a new event object with the (modified) data
      CustomLuaEvent result = new CustomLuaEvent(delegate.getName(), data);
      copies.put(luaObj, result);
      return result;
    }
    if (luaObj instanceof DelegatingProxy) {
      DelegatingProxy<?> proxy = (DelegatingProxy<?>) luaObj;
      if (!proxy.isTransferable()) {
        throw new IllegalArgumentException(String.format(
            "Can't transfer Lua object. Unsupported data type: %s", luaObj.getClass().getName()));
      }
      String classname = converters.getTypes().getClassname((Table) luaObj);
      if (classname != null) {
        return ((DelegatingProxy<?>) luaObj).getDelegate();
      } else {
        throw new IllegalArgumentException(String.format(
            "Can't transfer Lua object. Unsupported data type: %s", luaObj.getClass().getName()));
      }
    }
    if (luaObj instanceof Table) {
      Table table = (Table) luaObj;
      TableData result = transferTable(table, copies, converters);
      return result;
    }
    throw new IllegalArgumentException(
        String.format("Can't transfer Lua object. Unsupported data type: %s", Optional
            .of(converters.getTypes().getTypename(luaObj)).orElse(luaObj.getClass().getName())));
  }

  private static TableData transferTable(Table table, IdentityHashMap<Object, Object> copies,
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
    TraversableHashMap<Object, Object> contents = new TraversableHashMap<>();
    TableData result = new TableData(contents, typename);
    copies.put(table, result);

    for (Entry<Object, Object> entry : new TableIterable(table)) {
      Object newKey = transferObj(entry.getKey(), copies, converters);
      Object newValue = transferObj(entry.getValue(), copies, converters);
      contents.put(newKey, newValue);
    }
    return result;
  }


}
