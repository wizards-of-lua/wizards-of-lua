package net.wizardsoflua.lua.module.events;

import java.util.Collection;
import java.util.List;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.classes.eventqueue.EventQueue;
import net.wizardsoflua.lua.classes.eventsubscription.EventSubscription;
import net.wizardsoflua.lua.extension.api.function.NamedFunction2;
import net.wizardsoflua.lua.extension.api.function.NamedFunctionAnyArg;

public class EventsModule extends DelegatingProxy<EventHandlers> {
  public static EventsModule installInto(Table env, LuaClassLoader classLoader,
      EventHandlers eventHandlers) {
    EventsModule result = new EventsModule(classLoader, eventHandlers);
    env.rawset("Events", result);
    return result;
  }

  public EventsModule(LuaClassLoader classLoader, EventHandlers delegate) {
    super(classLoader, delegate);
    SubscribeFunction subscribeFunction = new SubscribeFunction();
    addImmutable(subscribeFunction.getName(), subscribeFunction);
    OnFunction onFunction = new OnFunction();
    addImmutable(onFunction.getName(), onFunction);
    ConnectFunction connectFunction = new ConnectFunction();
    addImmutable(connectFunction.getName(), connectFunction);
    FireFunction fireFunction = new FireFunction();
    addImmutable(fireFunction.getName(), fireFunction);
  }

  @Override
  public boolean isTransferable() {
    return false;
  }

  private class OnFunction extends NamedFunctionAnyArg {
    @Override
    public String getName() {
      return "on";
    }

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      List<String> eventNames = getConverters().toJavaList(String.class, args, getName());

      Table result = new DefaultTable();
      int idx = 1;
      for (String eventName : eventNames) {
        result.rawset(idx++, eventName);
      }

      Table metatable = new DefaultTable();
      metatable.rawset("__index", metatable);
      SubscribeFunction subscribeFunction = new SubscribeFunction();
      metatable.rawset("call", subscribeFunction);
      result.setMetatable(metatable);

      context.getReturnBuffer().setTo(result);
    }
  }

  private class SubscribeFunction extends NamedFunction2 {
    @Override
    public String getName() {
      return "subscribe";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) {
      Collection<String> eventNames =
          getConverters().toJavaList(String.class, arg1, 1, "eventNames", getName());
      LuaFunction eventHandler =
          getConverters().toJava(LuaFunction.class, arg2, 2, "eventHandler", getName());

      EventSubscription subscription = delegate.subscribe(eventNames, eventHandler);
      Object result = getConverters().toLua(subscription);
      context.getReturnBuffer().setTo(result);
    }
  }

  private class ConnectFunction extends NamedFunctionAnyArg {
    @Override
    public String getName() {
      return "connect";
    }

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      Collection<String> eventNames = getConverters().toJavaList(String.class, args, getName());
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
