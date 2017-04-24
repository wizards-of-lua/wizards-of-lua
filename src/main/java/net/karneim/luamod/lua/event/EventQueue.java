package net.karneim.luamod.lua.event;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayDeque;
import java.util.Deque;

public final class EventQueue {
  private final String type;
  private final Deque<Object> events = new ArrayDeque<>();

  public EventQueue(String type) {
    this.type = checkNotNull(type, "type==null!");
  }

  public String getType() {
    return type;
  }

  public void add(Object luaEvent) {
    checkNotNull(luaEvent, "luaEvent == null!");
    events.add(luaEvent);
  }

  public Object pop() {
    return events.pop();
  }

  public boolean hasNext() {
    return !events.isEmpty();
  }

  public void clear() {
    events.clear();
  }
}
