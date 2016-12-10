package net.karneim.luamod.lua.event;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayDeque;
import java.util.Deque;

public final class EventQueue {
  private final String type;
  private final Deque<EventWrapper<?>> events = new ArrayDeque<EventWrapper<?>>();

  public EventQueue(String type) {
    this.type = checkNotNull(type, "type==null!");
  }

  public String getType() {
    return type;
  }

  public void add(EventWrapper<?> event) {
    checkNotNull(event, "event==null!");
    checkArgument(type.equals(event.getType()),
        "event.type!=event.type: " + event.getType() + ", queue.type=" + getType());
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
