package net.wizardsoflua.lua.module.events;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.classes.eventqueue.EventQueue;
import net.wizardsoflua.lua.function.NamedFunction1;
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
    OnFunction registerFunction = new OnFunction();
    addImmutable(registerFunction.getName(), registerFunction);
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
      Collection<String> eventNames =
          getConverters().toJavaCollection(String.class, args, getName());


      Table result = new DefaultTable();
      RunFunction runFunction = new RunFunction(eventNames);
      result.rawset(runFunction.getName(), runFunction);
      context.getReturnBuffer().setTo(result);
    }
  }

  private class RunFunction extends NamedFunction1 {
    private final Collection<String> eventNames;

    public RunFunction(Collection<String> eventNames) {
      this.eventNames = checkNotNull(eventNames, "eventNames == null!");
    }

    @Override
    public String getName() {
      return "run";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      LuaFunction eventHandler =
          getConverters().toJava(LuaFunction.class, arg1, "eventHandler", getName());

      delegate.register(eventNames, eventHandler);
    }
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
