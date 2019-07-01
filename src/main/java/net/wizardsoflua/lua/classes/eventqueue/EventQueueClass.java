package net.wizardsoflua.lua.classes.eventqueue;

import static java.util.Objects.requireNonNull;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import net.minecraftforge.eventbus.api.Event;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaFunctionDoc;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.api.resource.LuaScheduler;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.LuaInstance;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.classes.event.EventClass;

/**
 * The <span class="notranslate">EventQueue</span> class collects [events](../Event) when it
 * is connected to the event source.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = EventQueueClass.NAME)
@GenerateLuaClassTable(instance = EventQueueClass.Instance.class)
@GenerateLuaDoc(subtitle = "Collecting Events")
public final class EventQueueClass
    extends BasicLuaClass<EventQueue, EventQueueClass.Instance<EventQueue>> {
  public static final String NAME = "EventQueue";
  @Resource
  private LuaConverters converters;
  @Resource
  private LuaScheduler scheduler;

  @Override
  protected Table createRawTable() {
    return new EventQueueClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<EventQueue>> toLuaInstance(EventQueue javaInstance) {
    return new EventQueueClassInstanceTable<>(new Instance<>(javaInstance), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends EventQueue> extends LuaInstance<D> {
    public Instance(D delegate) {
      super(delegate);
    }

    /**
     * These are the [names](../Event#name) of all events this queue is
     * [collecting](../Events#collect).
     */
    @LuaProperty
    public ImmutableSet<String> getNames() {
      return delegate.getNames();
    }

    /**
     * The 'stop' function stops collecting events into this queue.
     *
     * #### Example
     *
     * Collecting chat events and stopping it after the first event occurs.
     *
     * <code>
     * local queue = Events.collect("ChatEvent")
     * local event = queue:next()
     * print(str(event))
     * queue:stop()
     * </code>
     */
    @LuaFunction
    public void stop() {
      delegate.stop();
    }

    /**
     * The 'isEmpty' function returns true if this queue is empty, false otherwise.
     *
     * #### Example
     *
     * Busy-waiting for a chat event and printing the message when it occurs.
     *
     * <code>
     * local queue = Events.collect("ChatEvent")
     * while queue:isEmpty() do
     *   sleep(20)
     *   print("still waiting...")
     * end
     * local event = queue:next(0)
     * print("You said "..event.message)
     * </code>
     */
    @LuaFunction
    public boolean isEmpty() {
      return delegate.isEmpty();
    }

    /**
     * The 'latest' function returns the newest event in this queue and discards all older events.
     * If the queue [is empty](../EventQueue#isEmpty) then nil is returned. This is useful for
     * update events where you are only interested in the most recent change.
     *
     * #### Example
     *
     * Echo the last chat message every 5 seconds.
     *
     * <code>
     * local queue = Events.collect("ChatEvent")
     * while true do
     *   local event = queue:latest()
     *   if event ~= nil then
     *     spell:execute("say %s", event.message)
     *   end
     *   sleep(5 * 20)
     * end
     * </code>
     */
    @LuaFunction
    public Event latest() {
      Event latest = delegate.latest();
      delegate.clear();
      return latest;
    }

    /**
     * The 'next' function returns the next event in this queue, if any. This function blocks until
     * an event is available or the given timeout (measured in game ticks) is reached. If no timeout
     * is specified, this function blocks forever.
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
     */
    @LuaFunction(name = NextFunction.NAME)
    @LuaFunctionDoc(returnType = EventClass.NAME, args = {"timeout"})
    static class NextFunction extends AbstractFunction2 {
      public static final String NAME = "next";
      private final EventQueueClass luaClass;

      public NextFunction(EventQueueClass luaClass) {
        this.luaClass = requireNonNull(luaClass, "luaClass == null!");
      }

      @Override
      public void invoke(ExecutionContext context, Object arg1, Object arg2)
          throws ResolvedControlThrowable {
        EventQueue eventQueue = luaClass.converters.toJava(EventQueue.class, arg1, 1, "self", NAME);
        Long timeout = luaClass.converters.toJavaNullable(Long.class, arg2, 2, "timeout", NAME);
        eventQueue.waitForEvents(timeout);
        execute(context, eventQueue);
      }

      @Override
      public void resume(ExecutionContext context, Object suspendedState)
          throws ResolvedControlThrowable {
        EventQueue eventQueue = (EventQueue) suspendedState;
        execute(context, eventQueue);
      }

      private void execute(ExecutionContext context, EventQueue eventQueue)
          throws ResolvedControlThrowable {
        try {
          luaClass.scheduler.pauseIfRequested(context);
        } catch (UnresolvedControlThrowable e) {
          throw e.resolve(NextFunction.this, eventQueue);
        }

        if (!eventQueue.isEmpty()) {
          Object event = eventQueue.pop();
          Object result = luaClass.converters.toLua(event);
          context.getReturnBuffer().setTo(result);
        } else {
          context.getReturnBuffer().setTo();
        }
      }
    }
  }
}
