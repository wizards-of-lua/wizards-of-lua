package net.karneim.luamod.lua.event;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.sandius.rembulan.Table;

public abstract class EventWrapper<JavaObject> extends DelegatingTableWrapper<JavaObject> {
  private final String type;
  private final long id;
  private static long idCount = 0;

  public EventWrapper(Table env, @Nullable JavaObject javaObject, String type, Table metatable) {
    super(env, javaObject, metatable);
    id = ++idCount;
    this.type = checkNotNull(type, "type == null!");
  }

  public String getType() {
    return type;
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    builder.add("type", type);
    builder.add("id", id);
  }
}
