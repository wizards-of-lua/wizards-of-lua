package net.karneim.luamod.lua.event;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedList;

import javax.annotation.Nullable;

public final class EventListener {
  private final EventType type;
  private final LinkedList<Event> events = new LinkedList<Event>();

  public EventListener(EventType type) {
    this.type = checkNotNull(type, "type==null!");
  }

  public EventType getType() {
    return type;
  }

  public void receive(Event event) {
    events.add(checkNotNull(event, "event==null!"));
  }

  public @Nullable Event next() {
    if (hasNext()) {
      return events.removeFirst();
    }
    return null;
  }

  public boolean hasNext() {
    return !events.isEmpty();
  }
  
  public void clear() {
    events.clear();
  }
}
