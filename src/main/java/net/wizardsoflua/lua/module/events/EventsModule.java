package net.wizardsoflua.lua.module.events;

import java.util.ArrayList;
import java.util.List;

import com.google.auto.service.AutoService;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
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
import net.wizardsoflua.lua.data.Data;
import net.wizardsoflua.lua.extension.util.LuaTableExtension;
import net.wizardsoflua.lua.function.NamedFunction2;
import net.wizardsoflua.lua.function.NamedFunctionAnyArg;

@GenerateLuaModuleTable
@GenerateLuaDoc(name = EventsModule.NAME, subtitle = "Knowing What Happened")
@AutoService(SpellExtension.class)
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
  @Resource
  private LuaTypes types;

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

  @net.wizardsoflua.annotation.LuaFunction
  public void fire(String eventName, Object data) {
    // TODO Adrodoc 15.04.2018: Rewrite using proxies (see SpellEntity.data)
    Data data2 = Data.createData(data, types);
    MinecraftForge.EVENT_BUS.post(new CustomLuaEvent(eventName, data2));
  }

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
