package net.karneim.luamod.lua.event;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Event {
  private final EventType type;
  private final Object payload;

  public Event(EventType type, Object payload) {
    this.type = checkNotNull(type, "type==null!");
    this.payload = checkNotNull(payload, "payload==null!");
  }

  public EventType getType() {
    return type;
  }

  public Object getPayload() {
    return payload;
  }

}
