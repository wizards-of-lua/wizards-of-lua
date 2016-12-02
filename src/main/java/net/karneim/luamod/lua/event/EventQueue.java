package net.karneim.luamod.lua.event;

import static com.google.common.base.Preconditions.*;

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

  public void add(EventWrapper<?> event) {
    checkNotNull(event, "event==null!");
    checkArgument(event.getEventType() == type, "event.type!=queue.type");
    events.add(event);
  }

  public EventWrapper<?> pop() {
    return events.pop();
  }

  public boolean hasNext() {
    return !events.isEmpty();
  }

  public void clear() {
    events.clear();
  }
}
