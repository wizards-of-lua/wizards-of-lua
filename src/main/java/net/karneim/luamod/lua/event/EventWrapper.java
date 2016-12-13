package net.karneim.luamod.lua.event;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.wrapper.LuaWrapper;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.ImmutableTable;

public abstract class EventWrapper<JavaObject> extends LuaWrapper<JavaObject> {
  private final String type;
  private final long id;
  private static long idCount = 0;
  private Table luaObject = null;

  public EventWrapper(@Nullable JavaObject javaObject, String type) {
    super(javaObject);
    id = ++idCount;
    this.type = checkNotNull(type, "type == null!");
  }

  public String getType() {
    return type;
  }

  @Override
  protected final Table toLuaObject() {
    if (luaObject == null) {
      ImmutableTable.Builder builder = new ImmutableTable.Builder();
      toLuaObject(builder);
      luaObject = builder.build();
    }
    return luaObject;
  }

  protected void toLuaObject(ImmutableTable.Builder builder) {
    builder.add("type", type);
    builder.add("id", id);
  }
}
