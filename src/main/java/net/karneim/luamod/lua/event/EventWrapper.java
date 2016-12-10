package net.karneim.luamod.lua.event;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.wrapper.LuaWrapper;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;

public abstract class EventWrapper<JavaObject> extends LuaWrapper<JavaObject> {
  private final String type;
  private final long id;
  private static long idCount = 0;

  public EventWrapper(@Nullable JavaObject javaObject, String type) {
    super(javaObject);
    id = ++idCount;
    this.type = checkNotNull(type, "type == null!");
  }

  public String getType() {
    return type;
  }

  @Override
  protected Table toLuaObject() {
    Table result = new DefaultTable();
    result.rawset("type", type);
    result.rawset("id", id);
    return result;
  }
}
