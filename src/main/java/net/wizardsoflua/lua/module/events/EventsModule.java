package net.wizardsoflua.lua.module.events;

import java.util.Collection;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.classes.eventqueue.EventQueue;
import net.wizardsoflua.lua.function.NamedFunction2;
import net.wizardsoflua.lua.function.NamedFunctionAnyArg;

public class EventsModule extends DelegatingProxy<EventHandlers> {
  public static EventsModule installInto(Table env, LuaClassLoader classLoader,
      EventHandlers eventHandlers) {
    EventsModule result = new EventsModule(classLoader, eventHandlers);
    env.rawset("Events", result);
    return result;
  }

  public EventsModule(LuaClassLoader classLoader, EventHandlers delegate) {
    super(classLoader, null, delegate);
    ConnectFunction connectFunction = new ConnectFunction();
    addImmutable(connectFunction.getName(), connectFunction);
    FireFunction fireFunction = new FireFunction();
    addImmutable(fireFunction.getName(), fireFunction);
  }

  @Override
  public boolean isTransferable() {
    return false;
  }

  private class ConnectFunction extends NamedFunctionAnyArg {
    @Override
    public String getName() {
      return "connect";
    }

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      Collection<String> eventNames =
          getConverters().toJavaCollection(String.class, args, getName());
      EventQueue eventQueue = delegate.connect(eventNames);
      Object result = getConverters().toLua(eventQueue);
      context.getReturnBuffer().setTo(result);
    }
  }

  private class FireFunction extends NamedFunction2 {
    @Override
    public String getName() {
      return "fire";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) {
      String eventName = getConverters().toJava(String.class, arg1, 1, "eventName", getName());
      delegate.fire(eventName, arg2);
      context.getReturnBuffer().setTo();
    }
  }
}
