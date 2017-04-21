package net.karneim.luamod.lua.wrapper;

import java.util.ArrayList;
import java.util.List;

import net.karneim.luamod.lua.event.EventQueue;
import net.karneim.luamod.lua.event.EventQueuesWrapper;
import net.karneim.luamod.lua.event.EventType;
import net.karneim.luamod.lua.event.Events;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.AbstractFunctionAnyArg;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class EventsWrapper {

  public static EventsWrapper installInto(Table env, Events events) {
    EventsWrapper result = new EventsWrapper(events);
    env.rawset("Events", result.getLuaTable());
    return result;
  }

  private final Events events;
  private final Table luaTable = DefaultTable.factory().newTable();

  public EventsWrapper(Events events) {
    this.events = events;
    luaTable.rawset("register", new RegisterForEventsFunction());
    luaTable.rawset("fire", new FireEventFunction());
  }

  public Table getLuaTable() {
    return luaTable;
  }

  private class RegisterForEventsFunction extends AbstractFunctionAnyArg {

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      List<EventQueue> queues = new ArrayList<EventQueue>();
      for (Object arg : args) {
        String type = String.valueOf(arg);
        EventQueue queue = events.register(type);
        queues.add(queue);
      }
      EventQueuesWrapper wrapper = new EventQueuesWrapper(queues, events);
      context.getReturnBuffer().setTo(wrapper.getLuaTable());
    }

    private EventType getEventType(Object arg) {
      String name = String.valueOf(arg);
      return EventType.valueOf(name);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class FireEventFunction extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      String eventType = String.valueOf(arg1);
      Object content = arg2;
      events.fire(eventType, content);
      context.getReturnBuffer().setTo(null);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
