package net.wizardsoflua.lua.classes.eventqueue;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayDeque;
import java.util.Deque;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import net.minecraftforge.fml.common.eventhandler.Event;

public class EventQueue {

  public interface Context {
    void stop(EventQueue eventQueue);

    long getCurrentTime();
  }

  private final ImmutableSet<String> names;
  private final Context context;
  private Deque<Event> elements = new ArrayDeque<>();
  private long waitUntil = -1;

  public EventQueue(Iterable<String> names, Context context) {
    this.names = ImmutableSet.copyOf(names);
    this.context = checkNotNull(context, "context==null!");
  }

  public ImmutableSet<String> getNames() {
    return names;
  }

  public void stop() {
    context.stop(this);
  }

  public long getWaitUntil() {
    return waitUntil;
  }

  public void waitForEvents(@Nullable Long timeout) {
    if (timeout != null) {
      waitUntil = context.getCurrentTime() + timeout;
    } else {
      waitUntil = Long.MAX_VALUE;
    }
  }

  public boolean isEmpty() {
    return elements.isEmpty();
  }

  public void clear() {
    elements.clear();
  }

  public Event latest() {
    return elements.peekLast();
  }

  public Event pop() {
    Event result = elements.pop();
    stopWaitingForEvents();
    return result;
  }

  public void add(Event event) {
    elements.addLast(event);
  }

  private void stopWaitingForEvents() {
    waitUntil = -1;
  }
}
