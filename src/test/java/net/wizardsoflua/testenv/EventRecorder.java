package net.wizardsoflua.testenv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

/**
 * The {@link EventRecorder} listens for specific {@link Event (FML) Events} and records them. It
 * provides an API to wait for a specific event.
 */
public class EventRecorder {
  private final List<Event> events = new ArrayList<>();
  private final Object eventsSync = new Object();

  /**
   * Clears all recorded events.
   */
  public void clear() {
    events.clear();
  }

  /**
   * Blocks until an event of the specified type is received, and returns it. Removes the returned
   * event and any event that did occur before.
   * 
   * @param eventType
   * @return the first event of the specified type
   * @throws InterruptedException
   */
  public <E extends Event> E waitFor(Class<E> eventType, long duration, TimeUnit timeUnit)
      throws InterruptedException {
    long startTimeMs = System.currentTimeMillis();
    while (true) {
      synchronized (eventsSync) {
        while (true) {
          long now = System.currentTimeMillis();
          long durationLeft = timeUnit.toMillis(duration) - (now - startTimeMs);
          if (durationLeft < 0) {
            throw new RuntimeException("Timeout! Event " + eventType.getSimpleName()
                + " not occured within " + duration + " " + timeUnit);
          }
          if (events.isEmpty()) {
            eventsSync.wait(durationLeft);
          } else {
            break;
          }
        }
        Iterator<Event> it = events.iterator();
        while (it.hasNext()) {
          Event event = it.next();
          it.remove();
          if (eventType.isInstance(event)) {
            return eventType.cast(event);
          }
        }
      }
    }
  }

  @SubscribeEvent
  public void onEvent(ServerChatEvent evt) {
    addEvent(evt);
  }

  @SubscribeEvent
  public void onEvent(RightClickBlock evt) {
    // TODO can we remove this check?
    if (evt.getWorld().isRemote) {
      return;
    }
    addEvent(evt);
  }

  @SubscribeEvent
  public void onEvent(LeftClickBlock evt) {
    // TODO can we remove this check?
    if (evt.getWorld().isRemote) {
      return;
    }
    addEvent(evt);
  }

  @SubscribeEvent
  public void onEvent(TestPlayerReceivedChatEvent evt) {
    addEvent(evt);
  }

  @SubscribeEvent
  public void onEvent(ServerLog4jEvent evt) {
    addEvent(evt);
  }

  private void addEvent(Event evt) {
    synchronized (eventsSync) {
      events.add(evt);
      eventsSync.notify();
    }
  }
}
