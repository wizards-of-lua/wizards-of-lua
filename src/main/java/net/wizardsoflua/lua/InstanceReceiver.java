package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Map.Entry;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.lua.classes.LuaClass;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.table.TableIterable;

public class InstanceReceiver {
  private final Cache<Table, Table> cache = CacheBuilder.newBuilder().weakKeys().build();
  private final LuaClassLoader classLoader;

  public InstanceReceiver(LuaClassLoader classLoader) {
    this.classLoader = requireNonNull(classLoader, "classLoader == null!");
  }

  public Object receive(Object luaObject, LuaClassLoader sourceClassLoader) {
    checkArgument(sourceClassLoader != classLoader,
        "Source and target LuaClassLoader are identical");
    if (!isTransferable(luaObject)) {
      throw new IllegalArgumentException(
          String.format("Can't transfer Lua object. Unsupported data type: %s",
              sourceClassLoader.getTypes().getTypename(luaObject)));
    }
    if (luaObject instanceof DelegatingProxy) {
      Object delegate = ((DelegatingProxy<?>) luaObject).getDelegate();
      return classLoader.getConverters().toLua(delegate);
    }
    if (luaObject instanceof Table) {
      Table table = (Table) luaObject;
      return receiveTable(table, sourceClassLoader);
    }
    return luaObject;
  }

  private Table receiveTable(Table luaObject, LuaClassLoader sourceClassLoader) {
    Table result = cache.asMap().get(luaObject);
    if (result != null) {
      return result;
    }
    result = new DefaultTable();
    cache.put(luaObject, result);
    for (Entry<Object, Object> entry : new TableIterable(luaObject)) {
      Object newKey = receive(entry.getKey(), sourceClassLoader);
      Object newValue = receive(entry.getValue(), sourceClassLoader);
      result.rawset(newKey, newValue);
    }
    String className = sourceClassLoader.getTypes().getClassname(luaObject);
    if (className != null) {
      LuaClass targetClass = classLoader.getLuaClassForName(className);
      if (targetClass != null) {
        result.setMetatable(targetClass.getMetaTable());
      }
    }
    return result;
  }

  private static boolean isTransferable(Object luaObject) {
    if (luaObject instanceof LuaFunction) {
      // Currently we don't want functions to be transferable. If we want to support this we should
      // transfer all parameters and return values to the appropriate class loader.
      return false;
    }
    // TODO Adrodoc 20.03.2018: When branch fix/31 is merged use interface Transferable
    if (luaObject instanceof DelegatingProxy) {
      return ((DelegatingProxy<?>) luaObject).isTransferable();
    }
    return true;
  }
}
