package net.karneim.luamod.lua.event;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.wrapper.StructuredLuaWrapper;

public abstract class EventWrapper<JavaObject> extends StructuredLuaWrapper<JavaObject> {
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
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    builder.add("type", type);
    builder.add("id", id);
  }
}
