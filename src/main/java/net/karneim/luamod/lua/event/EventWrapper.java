package net.karneim.luamod.lua.event;

import static com.google.common.base.Preconditions.checkNotNull;

import net.karneim.luamod.lua.LuaWrapper;

public abstract class EventWrapper<JavaObject> extends LuaWrapper<JavaObject> {
  private final EventType eventType;

  public EventWrapper(JavaObject javaObject, EventType eventType) {
    super(javaObject);
    this.eventType = checkNotNull(eventType, "eventType == null!");
  }

  public EventType getEventType() {
    return eventType;
  }
}
