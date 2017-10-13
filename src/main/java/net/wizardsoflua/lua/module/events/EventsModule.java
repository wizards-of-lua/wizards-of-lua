package net.wizardsoflua.lua.module.events;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.AbstractFunctionAnyArg;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.classes.eventqueue.EventQueue;

public class EventsModule extends DelegatingProxy {

  public static EventsModule installInto(Table env, Converters converters,
      EventHandlers eventHandlers) {
    EventsModule result = new EventsModule(converters, eventHandlers);
    env.rawset("Events", result);
    return result;
  }

  private final EventHandlers delegate;

  public EventsModule(Converters converters, EventHandlers delegate) {
    super(converters, null, delegate);
    this.delegate = delegate;

    addImmutable("register", new RegisterFunction());
    addImmutable("fire", new FireFunction());
  }

  private class RegisterFunction extends AbstractFunctionAnyArg {

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      Iterable<String> eventNames = getConverters().toJavaIterableFromArray(String.class, args);
      EventQueue eventQueue = delegate.register(eventNames);
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
      String eventName = getConverters().toJava(String.class, arg1, "eventName");
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
