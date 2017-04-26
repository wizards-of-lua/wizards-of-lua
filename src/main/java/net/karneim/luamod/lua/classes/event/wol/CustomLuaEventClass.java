package net.karneim.luamod.lua.classes.event.wol;


import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.event.CustomLuaEvent;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.table.TableIterable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;

@LuaModule("CustomLuaEvent")
public class CustomLuaEventClass extends ImmutableLuaClass<CustomLuaEvent> {
  public CustomLuaEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder b, CustomLuaEvent event) {
    // overwite type defined in EventClass
    b.add("type", repo.wrap(event.getType()));
    b.add("data", copyData(event.getData()));
  }

  private @Nullable Object copyData(@Nullable Object data) {
    if (data == null) {
      return null;
    }
    if (data instanceof ByteString//
        || data instanceof String//
        || data instanceof Number//
        || data instanceof Boolean//
        || data instanceof DelegatingTable//
    ) {
      return data;
    }
    if (data instanceof Table) {
      Table table = (Table) data;
      Table result = DefaultTable.factory().newTable();
      for (Entry<Object, Object> entry : new TableIterable(table)) {
        Object key = copyData(entry.getKey());
        Object value = copyData(entry.getValue());
        result.rawset(key, value);
      }
      return result;
    }
    throw new IllegalArgumentException(
        "Unsupported event data type: " + data.getClass().getSimpleName());
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
