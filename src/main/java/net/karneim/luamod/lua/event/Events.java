package net.karneim.luamod.lua.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Events {

  private final Multimap<EventType, EventQueue> eventQueues = HashMultimap.create();
  private final Set<EventQueue> activeQueues = new HashSet<EventQueue>();

  private long currentTime;
  private long wakeUpTime;
  private long waitForEventUntil;

  public Events() {}

  public boolean isWaiting() {
    return isSleeping() || isWaitingForEvent();
  }

  public void startSleep(long duration) {
    this.wakeUpTime = currentTime + duration;
  }

  private boolean isSleeping() {
    return wakeUpTime > currentTime;
  }

  private boolean isWaitingForEvent() {
    if (waitForEventUntil <= currentTime || activeQueues.isEmpty())
      return false;
    for (EventQueue listener : activeQueues) {
      if (listener.hasNext()) {
        return false;
      }
    }
    return true;
  }

  public EventQueue register(EventType type) {
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
  
  public void handle(EventType type, Object evt) {
    Collection<EventQueue> queues = eventQueues.get(type);
    for (EventQueue queue : queues) {
      queue.add(type.wrap(evt));
    }
  }

}
