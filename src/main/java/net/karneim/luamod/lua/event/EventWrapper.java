package net.karneim.luamod.lua.event;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.wrapper.LuaWrapper;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;

public abstract class EventWrapper<JavaObject> extends LuaWrapper<JavaObject> {
  private final EventType eventType;

  public EventWrapper(@Nullable JavaObject javaObject, EventType eventType) {
    super(javaObject);
    this.eventType = checkNotNull(eventType, "eventType == null!");
  }

  public EventType getEventType() {
    return eventType;
  }

  @Override
  protected Table toLuaObject() {
    Table result = new DefaultTable();
    result.rawset("type", eventType.toString());
    return result;
  }
}
