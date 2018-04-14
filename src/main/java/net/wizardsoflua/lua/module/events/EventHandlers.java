package net.wizardsoflua.lua.module.events;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.event.CustomLuaEvent;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.eventinterceptor.EventInterceptor;
import net.wizardsoflua.lua.classes.eventqueue.EventQueue;
import net.wizardsoflua.lua.data.Data;

public class EventHandlers {

  public interface Context {
    long getCurrentTime();

    void call(LuaFunction function, Object... args);

    @Deprecated // TODO Adrodoc: There must be a better way to do this
    boolean isSpellTerminated();
  }

  private final Multimap<String, EventQueue> queues = HashMultimap.create();
  private final EventQueue.Context eventQueueContext = new EventQueue.Context() {
    @Override
    public void stop(EventQueue eventQueue) {
      EventHandlers.this.disconnect(eventQueue);
    }

    @Override
    public long getCurrentTime() {
      return context.getCurrentTime();
    }
  };
  /**
   * Using a linked multimap, because the order of subscriptions matters as later event listeners
   * are not called if the event was canceled by a previous one.
   */
  private final Multimap<String, EventInterceptor> subscriptions = LinkedHashMultimap.create();
  private final EventInterceptor.Context subscriptionContext = new EventInterceptor.Context() {
    @Override
    public void stop(EventInterceptor subscription) {
      for (String eventName : subscription.getEventNames()) {
        subscriptions.remove(eventName, subscription);
      }
    }
  };

  private final LuaClassLoader classLoader;
  private final Context context;

  public EventHandlers(LuaClassLoader classLoader, Context context) {
    this.classLoader = requireNonNull(classLoader, "classLoader == null!");
    this.context = requireNonNull(context, "context == null!");
  }

  /**
   * @return the value of {@link #subscriptions}
   */
  public Multimap<String, EventInterceptor> getSubscriptions() {
    return subscriptions;
  }

  public EventQueue connect(Iterable<String> eventNames) {
    EventQueue result = new EventQueue(eventNames, eventQueueContext);
    for (String name : eventNames) {
      queues.put(name, result);
    }
    return result;
  }

  public EventInterceptor subscribe(Iterable<String> eventNames, LuaFunction eventHandler) {
    return subscribe(new EventInterceptor(eventNames, eventHandler, subscriptionContext));
  }

  private EventInterceptor subscribe(EventInterceptor subscription) {
    for (String eventName : subscription.getEventNames()) {
      subscriptions.put(eventName, subscription);
    }
    return subscription;
  }

  public void disconnect(EventQueue queue) {
    for (String name : queue.getNames()) {
      queues.remove(name, queue);
    }
  }

  public boolean shouldPause() {
    if (queues.isEmpty()) {
      // no queues -> nothing to wait for, so keep running
      return false;
    }
    long now = context.getCurrentTime();

    for (EventQueue queue : queues.values()) {
      long waitUntil = queue.getWaitUntil();
      if (now <= waitUntil) {
        // we are still waiting for a message
        if (!queue.isEmpty()) {
          // but we have received one -> wake up
          return false;
        }
        // but we havn't yet received one -> pause
        return true;
      }
    }
    return false;
  }

  public void fire(String eventName, Object dataLuaObj) {
    Data data = Data.createData(dataLuaObj, classLoader);
    MinecraftForge.EVENT_BUS.post(new CustomLuaEvent(eventName, data));
  }

  public void onEvent(String eventName, Event event) {
    if (event.isCanceled()) {
      return;
    }
    // Avoid ConcurrentModificationException when unsubscribing within the interceptor
    List<EventInterceptor> subscriptions = new ArrayList<>(this.subscriptions.get(eventName));
    for (EventInterceptor subscription : subscriptions) {
      LuaFunction eventHandler = subscription.getEventHandler();
      Object luaEvent = classLoader.getConverters().toLua(event);
      context.call(eventHandler, luaEvent);
      if (event.isCanceled() || context.isSpellTerminated()) {
        return;
      }
    }
    for (EventQueue eventQueue : queues.get(eventName)) {
      eventQueue.add(event);
    }
  }
}
