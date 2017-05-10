package net.karneim.luamod.lua.event;

import static java.util.Collections.unmodifiableSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.karneim.luamod.LuaMod;
import net.karneim.luamod.lua.classes.LuaTypesRepo;

public class Events {
  private final ModEventHandler eventHandler = LuaMod.instance.getModEventHandler();
  private final LuaTypesRepo repo;
  private final Multimap<String, EventQueue> eventQueues = HashMultimap.create();
  private final Set<EventQueue> activeQueues = new HashSet<EventQueue>();

  private long currentTime;
  private long waitForEventUntil;

  public Events(LuaTypesRepo repo) {
    this.repo = Preconditions.checkNotNull(repo);
  }

  public LuaTypesRepo getRepo() {
    return repo;
  }

  public boolean isWaitingForEvent() {
    if (waitForEventUntil <= currentTime || activeQueues.isEmpty())
      return false;
    for (EventQueue listener : activeQueues) {
      if (listener.hasNext()) {
        return false;
      }
    }
    return true;
  }

  public EventQueue register(String type) {
    EventQueue result = new EventQueue(type);
    eventQueues.put(type, result);
    return result;
  }

  public boolean deregister(EventQueue queue) {
    return eventQueues.remove(queue.getType(), queue);
  }

  public void waitForEvents(Collection<? extends EventQueue> queues, int timeout) {
    activeQueues.clear();
    activeQueues.addAll(queues);
    waitForEventUntil = currentTime + timeout;
  }

  public void stopWaitingForEvent() {
    waitForEventUntil = currentTime;
    activeQueues.clear();
  }

  public void setCurrentTime(long currentTime) {
    this.currentTime = currentTime;
  }

  public Set<String> getRegisteredEventTypes() {
    return unmodifiableSet(eventQueues.keySet());
  }

  public void fire(String eventType, Object data) {
    // FIXME: Check that eventType is not a common event type like AnimationHandEvent or
    // RightClickBlockEvent

    CustomLuaEvent event = new CustomLuaEvent(eventType, data);
    eventHandler.onEvent(event);
  }

  public void handle(String eventType, Object luaEvent) {
    Collection<EventQueue> queues = eventQueues.get(eventType);
    if (!queues.isEmpty()) {
      for (EventQueue queue : queues) {
        queue.add(luaEvent);
      }
    }
  }
}
