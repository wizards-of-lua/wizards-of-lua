package net.wizardsoflua.testenv;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

/**
 * The {@link EventRecorder} listens for specific {@link Event (FML) Events} and records them. It
 * provides an API to wait for a specific event.
 */
public class EventRecorder {
  private final BlockingQueue<Event> events = new LinkedBlockingQueue<>();
  private volatile boolean enabled = false;

  /**
   * Clears all recorded events.
   */
  public void clear() {
    events.clear();
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Blocks until an event of the specified type is received, and returns it. Removes the returned
   * event and any event that did occur before.
   *
   * @param eventType
   * @return the first event of the specified type
   * @throws InterruptedException
   */
  public <E extends Event> E waitFor(Class<E> eventType, long timeout, TimeUnit unit)
      throws InterruptedException {
    if (!isEnabled()) {
      throw new IllegalStateException(EventRecorder.class.getSimpleName() + " is not enabled!");
    }
    long timeoutNanos = unit.toNanos(timeout);
    long startNanos = System.nanoTime();
    while (true) {
      long nanosLeft = timeoutNanos - (System.nanoTime() - startNanos);
      Event event = events.poll(nanosLeft, TimeUnit.NANOSECONDS);
      if (event == null) {
        throw new RuntimeException("Timeout! Event " + eventType.getSimpleName()
            + " not occured within " + timeout + " " + unit);
      }
      if (eventType.isInstance(event)) {
        return eventType.cast(event);
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
    if (isEnabled()) {
      events.add(evt);
    }
  }
}
