package net.wizardsoflua.lua.module.events;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.event.CustomLuaEvent;
import net.wizardsoflua.lua.classes.eventqueue.EventQueue;
import net.wizardsoflua.lua.classes.eventsubscription.EventSubscription;
import net.wizardsoflua.lua.data.Data;
import net.wizardsoflua.lua.extension.api.Config;
import net.wizardsoflua.lua.extension.api.Converter;
import net.wizardsoflua.lua.extension.api.ExceptionHandler;
import net.wizardsoflua.lua.extension.api.InitializationContext;
import net.wizardsoflua.lua.extension.api.LuaExtensionLoader;
import net.wizardsoflua.lua.extension.api.LuaScheduler;
import net.wizardsoflua.lua.extension.api.ParallelTaskFactory;
import net.wizardsoflua.lua.extension.api.PauseContext;
import net.wizardsoflua.lua.extension.api.Spell;
import net.wizardsoflua.lua.extension.api.Time;
import net.wizardsoflua.lua.module.types.Types;
import net.wizardsoflua.lua.module.types.TypesModule;

public class EventHandlers implements PauseContext {
  private final Converter converter;
  private final ExceptionHandler exceptionHandler;
  private final LuaScheduler scheduler;
  private final Time time;
  private final Types types;
  private final long luaTickLimit;
  private boolean duringEventIntercepting;

  public EventHandlers(InitializationContext context) {
    LuaExtensionLoader extensionLoader = context.getLuaExtensionLoader();
    TypesModule typesModule = extensionLoader.getLuaExtension(TypesModule.class);
    types = typesModule.getDelegate();
    converter = context.getConverter();
    exceptionHandler = context.getExceptionHandler();
    Config config = context.getConfig();
    luaTickLimit = config.getEventInterceptorTickLimit();
    scheduler = context.getScheduler();
    Spell spell = context.getSpell();
    spell.addParallelTaskFactory(new ParallelTaskFactory() {
      @Override
      public void terminate() {
        subscriptions.clear();
      }

      @Override
      public boolean isFinished() {
        return subscriptions.isEmpty();
      }
    });
    scheduler.addPauseContext(this);
    time = context.getTime();
  }

  private final Multimap<String, EventQueue> queues = HashMultimap.create();
  private final EventQueue.Context eventQueueContext = new EventQueue.Context() {
    @Override
    public void disconnect(EventQueue eventQueue) {
      EventHandlers.this.disconnect(eventQueue);
    }

    @Override
    public long getCurrentTime() {
      return time.getTotalWorldTime();
    }
  };
  /**
   * Using a linked multimap, because the order of subscriptions matters as later event listeners
   * are not called if the event was canceled by a previous one.
   */
  private final Multimap<String, EventSubscription> subscriptions = LinkedHashMultimap.create();
  private final EventSubscription.Context subscriptionContext = new EventSubscription.Context() {
    @Override
    public void unsubscribe(EventSubscription subscription) {
      for (String eventName : subscription.getEventNames()) {
        subscriptions.remove(eventName, subscription);
      }
    }
  };

  public EventQueue connect(Iterable<String> eventNames) {
    EventQueue result = new EventQueue(eventNames, eventQueueContext);
    for (String name : eventNames) {
      queues.put(name, result);
    }
    return result;
  }

  public EventSubscription subscribe(Iterable<String> eventNames, LuaFunction eventHandler) {
    return subscribe(new EventSubscription(eventNames, eventHandler, subscriptionContext));
  }

  private EventSubscription subscribe(EventSubscription subscription) {
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

  @Override
  public boolean shouldPause() {
    if (queues.isEmpty()) {
      // no queues -> nothing to wait for, so keep running
      return false;
    }
    long now = time.getTotalWorldTime();

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
    Data data = Data.createData(dataLuaObj, types);
    MinecraftForge.EVENT_BUS.post(new CustomLuaEvent(eventName, data));
  }

  public void onEvent(String eventName, Event event) {
    if (event.isCanceled()) {
      return;
    }
    // Avoid ConcurrentModificationException when unsubscribing within the interceptor
    List<EventSubscription> subscriptions = new ArrayList<>(this.subscriptions.get(eventName));
    for (EventSubscription subscription : subscriptions) {
      LuaFunction eventHandler = subscription.getEventHandler();
      Object luaEvent = converter.toLua(event);
      try {
        callDuringEventIntercepting(eventHandler, luaEvent);
      } catch (CallException | InterruptedException ex) {
        exceptionHandler.handle("Error in event interceptor", ex);
        return;
      }
      if (event.isCanceled()) {
        return;
      }
    }
    for (EventQueue eventQueue : queues.get(eventName)) {
      eventQueue.add(event);
    }
  }

  private void callDuringEventIntercepting(LuaFunction function, Object... args)
      throws CallException, InterruptedException {
    boolean wasDuringEventIntercepting = duringEventIntercepting;
    duringEventIntercepting = true;
    try {
      scheduler.callUnpausable(luaTickLimit, function, args);
    } finally {
      duringEventIntercepting = wasDuringEventIntercepting;
    }
  }

  public boolean isDuringEventIntercepting() {
    return duringEventIntercepting;
  }
}
