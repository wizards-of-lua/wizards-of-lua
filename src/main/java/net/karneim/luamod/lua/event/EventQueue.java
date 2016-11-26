package net.karneim.luamod.lua.event;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayDeque;
import java.util.Deque;

public final class EventQueue {
  private final EventType type;
  private final Deque<EventWrapper<?>> events = new ArrayDeque<EventWrapper<?>>();

  public EventQueue(EventType type) {
    this.type = checkNotNull(type, "type==null!");
  }

  public EventType getType() {
    return type;
  }

  public void receive(EventWrapper<?> event) {
    checkNotNull(event, "event==null!");
    if (type == event.getEventType()) {
      events.add(event);
    }
  }

  public EventWrapper<?> next() {
    return events.pop();
  }

  public boolean hasNext() {
    return !events.isEmpty();
  }

  public void clear() {
    events.clear();
  }
}
