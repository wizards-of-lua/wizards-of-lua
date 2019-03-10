package net.wizardsoflua.lua.module.events;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.auto.service.AutoService;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.TableFactory;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunctionDoc;
import net.wizardsoflua.event.CustomLuaEvent;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.ParallelTaskFactory;
import net.wizardsoflua.extension.spell.api.resource.Config;
import net.wizardsoflua.extension.spell.api.resource.ExceptionHandler;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.api.resource.LuaScheduler;
import net.wizardsoflua.extension.spell.api.resource.LuaTypes;
import net.wizardsoflua.extension.spell.api.resource.Spell;
import net.wizardsoflua.extension.spell.api.resource.Time;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.classes.eventinterceptor.EventInterceptor;
import net.wizardsoflua.lua.classes.eventinterceptor.EventInterceptorClass;
import net.wizardsoflua.lua.classes.eventqueue.EventQueue;
import net.wizardsoflua.lua.classes.eventqueue.EventQueueClass;
import net.wizardsoflua.lua.extension.LuaTableExtension;
import net.wizardsoflua.lua.function.NamedFunction2;
import net.wizardsoflua.lua.function.NamedFunctionAnyArg;
import net.wizardsoflua.lua.view.ViewFactory;

/**
 * The <span class="notranslate">Events</span> module provides functions for accessing and firing
 * [Events](/modules/Event/).
 */
@AutoService(SpellExtension.class)
@GenerateLuaModuleTable
@GenerateLuaDoc(name = EventsModule.NAME, subtitle = "Knowing What Happened")
public class EventsModule extends LuaTableExtension {
  public static final String NAME = "Events";
  @Resource
  private LuaConverters converters;
  @Resource
  private ExceptionHandler exceptionHandler;
  @Resource
  private LuaScheduler scheduler;
  @Resource
  private TableFactory tableFactory;
  @Resource
  private Time time;
  @Inject
  private ViewFactory viewFactory;

  private final Multimap<String, EventQueue> queues = HashMultimap.create();
  private final EventQueue.Context eventQueueContext = new EventQueue.Context() {
    @Override
    public void stop(EventQueue queue) {
      for (String name : queue.getNames()) {
        queues.remove(name, queue);
      }
    }

    @Override
    public long getCurrentTime() {
      return time.getTotalWorldTime();
    }
  };
  /**
   * Using a linked multimap, because the order of interceptors matters as later event interceptors
   * are not called if the event was canceled by a previous one.
   */
  private final Multimap<String, EventInterceptor> interceptors = LinkedHashMultimap.create();
  private final EventInterceptor.Context interceptorContext = new EventInterceptor.Context() {
    @Override
    public void stop(EventInterceptor interceptor) {
      for (String eventName : interceptor.getEventNames()) {
        interceptors.remove(eventName, interceptor);
      }
    }
  };
  private long luaTickLimit;
  private boolean duringEventIntercepting;

  public void initialize(@Resource Config config, @Resource Spell spell) {
    luaTickLimit = config.getEventInterceptorTickLimit();
    spell.addParallelTaskFactory(new ParallelTaskFactory() {
      @Override
      public void terminate() {
        interceptors.clear();
      }

      @Override
      public boolean isFinished() {
        return interceptors.isEmpty();
      }
    });
    scheduler.addPauseContext(this::shouldPause);
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table getTable() {
    return new EventsModuleTable<>(this, converters);
  }

  /**
   * The 'fire' function posts a new [CustomEvent](/modules/CustomEvent/) with the given name and
   * the optional given content data.
   *
   * #### Example
   *
   * Firing a custom event without any data.
   *
   * <code>
   * Events.fire("my-event")
   * </code>
   *
   * #### Example
   *
   * Registering an event intereptor for a custom event that prints the event data.
   *
   * <code>
   * Events.on("my-event"):call(function(event)
   *   print(str(event.data))
   * end)
   * </code>
   *
   * Firing a custom event with some data.
   *
   * <code>
   * local data = spell.block
   * Events.fire("my-event", data)
   * </code>
   */
  @net.wizardsoflua.annotation.LuaFunction
  public void fire(String eventName, Object data) {
    MinecraftForge.EVENT_BUS.post(new CustomLuaEvent(eventName, data, viewFactory));
  }

  /**
   * The 'collect' function creates an [EventQueue](/modules/EventQueue/) that collects all
   * [Event](/modules/Event) occurrences of the specified kind(s).
   *
   * #### Example
   *
   * Echoing all chat messages.
   *
   * <code>
   * local queue = Events.collect("ChatEvent")
   * while true do
   *   local event = queue:next()
   *   spell:execute("say %s", event.message)
   * end
   * </code>
   *
   * #### Example
   *
   * Posting the position of all block-click events into the chat.
   *
   * <code>
   * local queue=Events.collect("LeftClickBlockEvent","RightClickBlockEvent")
   * while true do
   *   local event = queue:next()
   *   spell:execute("say %s at %s", event.name, event.pos)
   * end
   * </code>
   */
  @net.wizardsoflua.annotation.LuaFunction(name = CollectFunction.NAME)
  @LuaFunctionDoc(returnType = EventQueueClass.NAME, args = {"eventName..."})
  class CollectFunction extends NamedFunctionAnyArg {
    public static final String NAME = "collect";

    @Override
    public String getName() {
      return NAME;
    }

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      List<String> eventNames = converters.toJavaList(String.class, args, getName());
      EventQueue result = collect(eventNames);
      Object luaResult = converters.toLua(result);
      context.getReturnBuffer().setTo(luaResult);
    }
  }

  public EventQueue collect(Iterable<String> eventNames) {
    EventQueue result = new EventQueue(eventNames, eventQueueContext);
    for (String name : eventNames) {
      queues.put(name, result);
    }
    return result;
  }

  /**
   * Returns a table containing the specified event names and a reference to
   * [intercept](#intercept). This can be used as shorthand for [intercept](#intercept).
   *
   * #### Example
   *
   * Subscribing for chat events and printing the messages.
   *
   * <code>
   * local interceptor = Events.on('ChatEvent'):call( function(event)
   *   print(event.message)
   * end)
   * </code>
   */
  @net.wizardsoflua.annotation.LuaFunction(name = OnFunction.NAME)
  @LuaFunctionDoc(returnType = LuaTypes.TABLE, args = {"eventName..."})
  class OnFunction extends NamedFunctionAnyArg {
    public static final String NAME = "on";

    @Override
    public String getName() {
      return NAME;
    }

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      List<String> eventNames = converters.toJavaList(String.class, args, getName());
      Table result = on(eventNames);
      context.getReturnBuffer().setTo(result);
    }
  }

  public Table on(Iterable<String> eventNames) {
    Table result = tableFactory.newTable();
    int idx = 1;
    for (String eventName : eventNames) {
      result.rawset(idx++, eventName);
    }

    Table metatable = tableFactory.newTable();
    metatable.rawset("__index", metatable);
    InterceptFunction interceptFunction = new InterceptFunction();
    metatable.rawset("call", interceptFunction);
    result.setMetatable(metatable);
    return result;
  }

  /**
   * Creates an event interceptor for [Events](/module/Event) with the specified names.
   *
   * The interceptor will be called immediately when an event occurs, which allows events to be
   * modified and [canceled](/modules/Event#canceled).
   *
   * Event interceptors do not support [sleeping](/modules/Time#sleep) - therefor,
   * [autosleep](/modules/Time#autosleep) is disabled and manual sleeping is treated as an illegal
   * operation.
   *
   * As long as a [Spell](/modules/Spell) has any active event interceptors it will not terminate by
   * itself, so make sure to [stop](/modules/EventInterceptor#stop) each event interceptor that is
   * no longer needed.
   *
   * #### Example
   *
   * Intercepting chat events.
   *
   * <code>
   * local interceptor =
   * Events.intercept({'ChatEvent'}, function(event)
   *   print(str(event))
   * end )
   * </code>
   *
   * #### *Warning: Beware of possible race conditions!*
   *
   * Be careful, when accessing variables that are used both by the main program as well as by the
   * event interceptor.
   *
   * If [autosleep](/modules/Time#autosleep) is enabled, the main program can fall asleep eventually
   * at any time, which allows that a variable might be modified in an awkward situation.
   *
   * For instance, the following program fails due to indexing a nil value in line 10 despite the
   * nil check in line 8.
   *
   * In this example there is an explicit sleep in line 9, but that sleep could just as well be
   * caused by [autosleep](/modules/Time#autosleep).
   *
   * <code>
   * local abc = 'abc'
   * local interceptor = Events.intercept({'my-event'}, function(event)
   *   abc = nil
   * end)
   * spell:execute([[lua
   *   Events.fire('my-event')
   * ]])
   * if abc ~= nil then
   *   sleep(1)
   *   print(abc:len())
   * end
   * interceptor:stop()
   * </code>
   */
  @net.wizardsoflua.annotation.LuaFunction(name = InterceptFunction.NAME)
  @LuaFunctionDoc(returnType = EventInterceptorClass.NAME, args = {"eventNames", "eventHandler"})
  class InterceptFunction extends NamedFunction2 {
    public static final String NAME = "intercept";

    @Override
    public String getName() {
      return NAME;
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      Iterable<String> eventNames =
          converters.toJavaList(String.class, arg1, 1, "eventNames", getName());
      LuaFunction eventHandler =
          converters.toJava(LuaFunction.class, arg2, 2, "eventHandler", getName());
      EventInterceptor result = intercept(eventNames, eventHandler);
      Object luaResult = converters.toLuaNullable(result);
      context.getReturnBuffer().setTo(luaResult);
    }
  }

  public EventInterceptor intercept(Iterable<String> eventNames, LuaFunction eventHandler) {
    return intercept(new EventInterceptor(eventNames, eventHandler, interceptorContext));
  }

  private EventInterceptor intercept(EventInterceptor interceptor) {
    for (String eventName : interceptor.getEventNames()) {
      interceptors.put(eventName, interceptor);
    }
    return interceptor;
  }

  public boolean shouldPause() {
    if (queues.isEmpty()) {
      // no queues -> nothing to wait for, so keep running
      return false;
    }
    long now = time.getTotalWorldTime();

    for (EventQueue queue : queues.values()) {
      long waitUntil = queue.getWaitUntil();
      if (now < waitUntil) {
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

  public void onEvent(String eventName, Event event) {
    if (event.isCanceled()) {
      return;
    }
    // Avoid ConcurrentModificationException when stopping during interception
    List<EventInterceptor> interceptors = new ArrayList<>(this.interceptors.get(eventName));
    for (EventInterceptor interceptor : interceptors) {
      LuaFunction eventHandler = interceptor.getEventHandler();
      Object luaEvent = converters.toLua(event);
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
