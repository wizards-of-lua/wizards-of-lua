package net.wizardsoflua.lua.classes.eventqueue;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.function.NamedFunction1;
import net.wizardsoflua.lua.function.NamedFunction2;

@DeclareLuaClass(name = EventQueueClass.NAME)
public class EventQueueClass
    extends ProxyingLuaClass<EventQueue, EventQueueClass.Proxy<EventQueue>> {
  public static final String NAME = "EventQueue";

  public EventQueueClass() {
    add(new DisconnectFunction());
    add(new IsEmptyFunction());
    add(new LatestFunction());
    add(new NextFunction());
  }

  @Override
  public Proxy<EventQueue> toLua(EventQueue javaObj) {
    return new Proxy<>(getConverters(), getMetaTable(), javaObj);
  }

  public static class Proxy<D extends EventQueue> extends DelegatingProxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addReadOnly("names", this::getNames);
    }

    @Override
    public boolean isTransferable() {
      return false;
    }

    private Table getNames() {
      Iterable<String> eventTypes = delegate.getNames();
      return getConverters().toLuaIterable(eventTypes);
    }
  }

  private class DisconnectFunction extends NamedFunction1 {
    @Override
    public String getName() {
      return "disconnect";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) {
      EventQueue eventQueue = getConverters().toJava(EventQueue.class, arg1, 1, "self", getName());
      eventQueue.disconnect();
      context.getReturnBuffer().setTo();
    }
  }

  private class IsEmptyFunction extends NamedFunction1 {
    @Override
    public String getName() {
      return "isEmpty";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) {
      EventQueue eventQueue = getConverters().toJava(EventQueue.class, arg1, 1, "self", getName());
      boolean isEmpty = eventQueue.isEmpty();
      Object result = getConverters().toLua(isEmpty);
      context.getReturnBuffer().setTo(result);
    }
  }

  private class LatestFunction extends NamedFunction1 {
    @Override
    public String getName() {
      return "latest";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      EventQueue eventQueue = getConverters().toJava(EventQueue.class, arg1, 1, "self", getName());
      Object event = eventQueue.latest();
      eventQueue.clear();
      Object result = getConverters().toLuaNullable(event);
      context.getReturnBuffer().setTo(result);
    }
  }

  private class NextFunction extends NamedFunction2 {
    @Override
    public String getName() {
      return "next";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      EventQueue eventQueue = getConverters().toJava(EventQueue.class, arg1, 1, "self", getName());
      Long timeout = getConverters().toJavaNullable(Long.class, arg2, 2, "timeout", getName());
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
        context.pauseIfRequested();
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
