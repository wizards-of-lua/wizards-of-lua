package net.wizardsoflua.lua.module.events;

import java.util.Collection;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.AbstractFunctionAnyArg;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.classes.eventqueue.EventQueue;

public class EventsModule extends DelegatingProxy<EventHandlers> {
  public static EventsModule installInto(Table env, Converters converters,
      EventHandlers eventHandlers) {
    EventsModule result = new EventsModule(converters, eventHandlers);
    env.rawset("Events", result);
    return result;
  }

  public EventsModule(Converters converters, EventHandlers delegate) {
    super(converters, null, delegate);
    addImmutable(ConnectFunction.NAME, new ConnectFunction());
    addImmutable("fire", new FireFunction());
  }

  @Override
  public boolean isTransferable() {
    return false;
  }

  private class ConnectFunction extends AbstractFunctionAnyArg {
    public static final String NAME = "connect";

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      Collection<String> eventNames = getConverters().toJavaCollection(String.class, args, NAME);
      EventQueue eventQueue = delegate.connect(eventNames);
      Object result = getConverters().toLua(eventQueue);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class FireFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) {
      String eventName = getConverters().toJavaOld(String.class, arg1, "eventName");
      delegate.fire(eventName, arg2);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
}
