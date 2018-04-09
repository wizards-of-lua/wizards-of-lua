package net.wizardsoflua.lua.classes.eventqueue;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaFunctionDoc;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.LuaClass;
import net.wizardsoflua.lua.classes.LuaClassApi;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;
import net.wizardsoflua.lua.classes.event.EventClass;

/**
 * The <span class="notranslate">EventQueue</span> class collects [events](/modules/Event) when it
 * is connected to the event source.
 */
@GenerateLuaClass(name = "EventQueue")
@GenerateLuaDoc(subtitle = "Collecting Events")
public class EventQueueApi<D extends EventQueue> extends LuaClassApi<D> {
  public EventQueueApi(DelegatorLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }

  /**
   * These are the [names](/modules/Event#name) of all events this queue is
   * [connected](/modules/Events#connect) to.
   */
  @LuaProperty
  public ImmutableSet<String> getNames() {
    return delegate.getNames();
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
    delegate.disconnect();
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
    return delegate.isEmpty();
  }

  /**
   * The 'latest' function returns the newest event in this queue and discards all older events. If
   * the queue [is empty](/modules/EventQueue#isEmpty) then nil is returned. This is useful for
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
    Event latest = delegate.latest();
    delegate.clear();
    return latest;
  }

  /**
   * The 'next' function returns the next event in this queue, if any. This function blocks until an
   * event is available or the given timeout (measured in game ticks) is reached. If no timeout is
   * specified, this function blocks forever.
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
    private final LuaClass luaClass;

    public NextFunction(LuaClass luaClass) {
      this.luaClass = checkNotNull(luaClass, "luaClass == null!");
    }

    private Converters getConverters() {
      return luaClass.getClassLoader().getConverters();
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      EventQueue eventQueue = getConverters().toJava(EventQueue.class, arg1, 1, "self", NAME);
      Long timeout = getConverters().toJavaNullable(Long.class, arg2, 2, "timeout", NAME);
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
        luaClass.getClassLoader().getCurrentSchedulingContext().pauseIfRequested(context);
      } catch (UnresolvedControlThrowable e) {
        throw e.resolve(NextFunction.this, eventQueue);
      }

      if (!eventQueue.isEmpty()) {
        Object event = eventQueue.pop();
        Object result = getConverters().toLua(event);
        context.getReturnBuffer().setTo(result);
      } else {
        context.getReturnBuffer().setTo();
      }
    }
  }
}
