package net.wizardsoflua.lua.classes.eventqueue;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayDeque;
import java.util.Deque;

import com.google.common.collect.ImmutableSet;

public class EventQueue {

  public interface Context {
    void unregister(EventQueue eventQueue);

    long getCurrentTime();
  }

  private final ImmutableSet<String> names;
  private final Context context;
  private Deque<Object> elements = new ArrayDeque<>();
  private long waitUntil = -1;

  public EventQueue(Iterable<String> names, Context context) {
    this.names = ImmutableSet.copyOf(names);
    this.context = checkNotNull(context, "context==null!");
  }

  public ImmutableSet<String> getNames() {
    return names;
  }

  public void unregister() {
    context.unregister(this);
  }

  public long getWaitUntil() {
    return waitUntil;
  }

  public void waitForEvents(Long timeout) {
    if (timeout != null) {
      waitUntil = context.getCurrentTime() + timeout;
    } else {
      waitUntil = Long.MAX_VALUE;
    }
  }

  public boolean isEmpty() {
    return elements.isEmpty();
  }

  public Object pop() {
    Object result = elements.pop();
    stopWaitingForEvents();
    return result;
  }

  public void add(Object event) {
    elements.addLast(event);
  }

  private void stopWaitingForEvents() {
    waitUntil = -1;
  }

}
