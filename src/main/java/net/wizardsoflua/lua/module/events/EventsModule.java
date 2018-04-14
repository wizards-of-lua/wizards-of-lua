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
import net.wizardsoflua.lua.classes.eventinterceptor.EventInterceptor;
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
    super(classLoader, delegate);
    InterceptFunction interceptFunction = new InterceptFunction();
    addImmutable(interceptFunction.getName(), interceptFunction);
    OnFunction onFunction = new OnFunction();
    addImmutable(onFunction.getName(), onFunction);
    CollectFunction collectFunction = new CollectFunction();
    addImmutable(collectFunction.getName(), collectFunction);
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
      InterceptFunction interceptFunction = new InterceptFunction();
      metatable.rawset("call", interceptFunction);
      result.setMetatable(metatable);

      context.getReturnBuffer().setTo(result);
    }
  }

  private class InterceptFunction extends NamedFunction2 {
    @Override
    public String getName() {
      return "intercept";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) {
      Collection<String> eventNames =
          getConverters().toJavaList(String.class, arg1, 1, "eventNames", getName());
      LuaFunction eventHandler =
          getConverters().toJava(LuaFunction.class, arg2, 2, "eventHandler", getName());

      EventInterceptor subscription = delegate.subscribe(eventNames, eventHandler);
      Object result = getConverters().toLua(subscription);
      context.getReturnBuffer().setTo(result);
    }
  }

  private class CollectFunction extends NamedFunctionAnyArg {
    @Override
    public String getName() {
      return "collect";
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
