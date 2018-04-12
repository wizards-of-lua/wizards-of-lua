package net.wizardsoflua.lua.classes.eventqueue.scribble;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableSet;

import net.minecraftforge.fml.common.eventhandler.Event;
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
import net.wizardsoflua.lua.classes.common.ModifiableDelegator;
import net.wizardsoflua.lua.classes.event.EventClass;
import net.wizardsoflua.lua.classes.eventqueue.EventQueue;
import net.wizardsoflua.lua.extension.api.InitializationContext;
import net.wizardsoflua.lua.extension.api.LuaScheduler;
import net.wizardsoflua.lua.extension.util.AbstractLuaClass;

/**
 * The <span class="notranslate">EventQueue</span> class collects [events](/modules/Event) when it
 * is connected to the event source.
 */
@GenerateLuaClassTable(instance = EventQueueClass.Instance.class)
@GenerateLuaDoc(name = EventQueueClass.NAME, subtitle = "Collecting Events")
public class EventQueueClass extends AbstractLuaClass<EventQueue, EventQueueClassInstanceTable> {
  public static final String NAME = "EventQueue2Class";
  private LuaScheduler scheduler;

  @Override
  public void initialize(InitializationContext context) {
    super.initialize(context);
    scheduler = context.getScheduler();
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table createTable() {
    return new EventQueueClassTable(this, getConverter());
  }

  @Override
  protected EventQueueClassInstanceTable toLuaInstance(EventQueue javaInstance) {
    return new EventQueueClassInstanceTable(new Instance<>(javaInstance), getConverter());
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends EventQueue> extends ModifiableDelegator<D> {
    public Instance(D delegate) {
      super(delegate);
    }

    /**
     * These are the [names](/modules/Event#name) of all events this queue is
     * [connected](/modules/Events#connect) to.
     */
    @LuaProperty
    public ImmutableSet<String> getNames() {
      return getDelegate().getNames();
    }

    /**
     * The 'disconnect' function disconnects this queue from the event source so that it will not
     * collect any events anymore.
     *
     * #### Example
     *
     * Connecting an event queue to the chat event source and disconnecting it after the first event
     * occurs.
     *
     * <code>
     * local queue = Events.connect("ChatEvent")
     * local event = queue:next()
     * print(str(event))
     * queue:disconnect()
     * </code>
     */
    @LuaFunction
    public void disconnect() {
      getDelegate().disconnect();
    }

    /**
     * The 'isEmpty' function returns true if this queue is empty, false otherwise.
     *
     * #### Example
     *
     * Busy-waiting for a chat event and printing the message when it occurs.
     *
     * <code>
     * local queue = Events.connect("ChatEvent")
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
      return getDelegate().isEmpty();
    }

    /**
     * The 'latest' function returns the newest event in this queue and discards all older events.
     * If the queue [is empty](/modules/EventQueue#isEmpty) then nil is returned. This is useful for
     * update events where you are only interested in the most recent change.
     *
     * #### Example
     *
     * Echo the last chat message every 5 seconds.
     *
     * <code>
     * local queue = Events.connect("ChatEvent")
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
      D delegate = getDelegate();
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
     * local queue = Events.connect("ChatEvent")
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
        EventQueue eventQueue =
            luaClass.getConverter().toJava(EventQueue.class, arg1, 1, "self", NAME);
        Long timeout = luaClass.getConverter().toJavaNullable(Long.class, arg2, 2, "timeout", NAME);
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
          Object result = luaClass.getConverter().toLua(event);
          context.getReturnBuffer().setTo(result);
        } else {
          context.getReturnBuffer().setTo();
        }
      }
    }
  }
}
