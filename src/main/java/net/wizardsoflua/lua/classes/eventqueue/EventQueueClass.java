package net.wizardsoflua.lua.classes.eventqueue;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.LuaClass;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;

@DeclareLuaClass(name = EventQueueClass.METATABLE_NAME)
public class EventQueueClass extends LuaClass<EventQueue> {
  public static final String METATABLE_NAME = "EventQueue";

  public EventQueueClass() {
    super(EventQueue.class);
    add("isEmpty", new IsEmptyFunction());
    add("pop", new PopFunction());
    add("unregister", new UnregisterFunction());
  }

  @Override
  public Table toLua(EventQueue javaObj) {
    return new Proxy(getConverters(), getMetatable(), javaObj);
  }

  @Override
  public EventQueue toJava(Table luaObj) {
    Proxy proxy = getProxy(luaObj);
    return proxy.delegate;
  }

  protected Proxy getProxy(Object luaObj) {
    getConverters().getTypes().checkAssignable(METATABLE_NAME, luaObj);
    return (Proxy) luaObj;
  }

  public static class Proxy extends DelegatingProxy {

    private final EventQueue delegate;

    public Proxy(Converters converters, Table metatable, EventQueue delegate) {
      super(converters, metatable, delegate);
      this.delegate = delegate;
      addReadOnly("names", this::getNames);
    }

    private Table getNames() {
      Iterable<String> eventTypes = delegate.getNames();
      return getConverters().toLuaIterable(eventTypes);
    }
  }

  private class IsEmptyFunction extends AbstractFunction1 {
    @Override
    public void invoke(ExecutionContext context, Object arg1) {
      EventQueue eventQueue = getConverters().toJava(EventQueue.class, arg1);
      boolean isEmpty = eventQueue.isEmpty();
      Object result = getConverters().toLua(isEmpty);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState) {
      throw new NonsuspendableFunctionException();
    }
  }

  private class PopFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      EventQueue eventQueue = getConverters().toJava(EventQueue.class, arg1);
      Long gameticksTimeout = getConverters().toJavaNullable(Long.class, arg2);
      eventQueue.waitForEvents(gameticksTimeout);
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
        context.pauseIfRequested();
      } catch (UnresolvedControlThrowable e) {
        throw e.resolve(PopFunction.this, eventQueue);
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

  private class UnregisterFunction extends AbstractFunction1 {
    @Override
    public void invoke(ExecutionContext context, Object arg1) {
      EventQueue eventQueue = getConverters().toJava(EventQueue.class, arg1);
      eventQueue.unregister();
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState) {
      throw new NonsuspendableFunctionException();
    }
  }

}
