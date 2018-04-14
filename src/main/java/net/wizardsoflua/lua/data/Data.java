package net.wizardsoflua.lua.data;

import java.util.IdentityHashMap;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.util.TraversableHashMap;
import net.wizardsoflua.event.CustomLuaEvent;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.classes.event.CustomEventClass;
import net.wizardsoflua.lua.module.types.TypesModule;
import net.wizardsoflua.lua.table.TableIterable;

public class Data {
  public static Data empty() {
    return new Data(null);
  }

  public static Data createData(Object javaObj) {
    return new Data(javaObj);
  }

  public static Data createData(Object luaObj, TypesModule types) {
    return new Data(transferObj(luaObj, new IdentityHashMap<>(), types));
  }

  private final @Nullable Object content;

  private Data(@Nullable Object content) {
    this.content = content;
  }

  public @Nullable Object getContent() {
    return content;
  }

  private static Object transferObj(Object luaObj, IdentityHashMap<Object, Object> copies,
      TypesModule types) {
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
      return luaObj.toString();
    }
    if (luaObj instanceof CustomEventClass.Proxy) {
      CustomEventClass.Proxy<?> proxy = (CustomEventClass.Proxy<?>) luaObj;
      CustomLuaEvent delegate = proxy.getDelegate();
      Data data = Data.createData(proxy.rawget("data"), types);
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
      return proxy.getDelegate();
    }
    if (luaObj instanceof Table) {
      Table table = (Table) luaObj;
      TableData result = transferTable(table, copies, types);
      return result;
    }
    throw new IllegalArgumentException(String
        .format("Can't transfer Lua object. Unsupported data type: %s", types.getTypename(luaObj)));
  }

  private static TableData transferTable(Table table, IdentityHashMap<Object, Object> copies,
      TypesModule types) {
    if (table == null) {
      return null;
    }
    Object copy = copies.get(table);
    if (copy != null) {
      TableData result = (TableData) copy;
      return result;
    }

    TraversableHashMap<Object, Object> contents = new TraversableHashMap<>();
    String classname = types.getClassname(table);
    TableData result = new TableData(contents, classname);
    copies.put(table, result);

    for (Entry<Object, Object> entry : new TableIterable(table)) {
      Object newKey = transferObj(entry.getKey(), copies, types);
      Object newValue = transferObj(entry.getValue(), copies, types);
      contents.put(newKey, newValue);
    }
    return result;
  }
}
