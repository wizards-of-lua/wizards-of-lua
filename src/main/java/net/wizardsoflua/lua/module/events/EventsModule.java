package net.wizardsoflua.lua.module.events;

import java.util.Collection;
import java.util.List;

import com.google.auto.service.AutoService;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.classes.eventqueue.EventQueue;
import net.wizardsoflua.lua.classes.eventsubscription.EventSubscription;
import net.wizardsoflua.lua.extension.api.Converter;
import net.wizardsoflua.lua.extension.api.InitializationContext;
import net.wizardsoflua.lua.extension.api.function.NamedFunction2;
import net.wizardsoflua.lua.extension.api.function.NamedFunctionAnyArg;
import net.wizardsoflua.lua.extension.spi.LuaExtension;
import net.wizardsoflua.lua.extension.util.AbstractLuaModule;

@AutoService(LuaExtension.class)
public class EventsModule extends AbstractLuaModule {
  private Table table;
  private Converter converter;
  private EventHandlers delegate;

  @Override
  public void initialize(InitializationContext context) {
    table = context.getTableFactory().newTable();
    converter = context.getConverter();
    delegate = new EventHandlers(context);
    add(new SubscribeFunction());
    add(new OnFunction());
    add(new ConnectFunction());
    add(new FireFunction());
  }

  public EventHandlers getDelegate() {
    return delegate;
  }

  @Override
  public String getName() {
    return "Events";
  }

  @Override
  public Table getLuaObject() {
    return table;
  }

  private class OnFunction extends NamedFunctionAnyArg {
    @Override
    public String getName() {
      return "on";
    }

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      List<String> eventNames = converter.toJavaList(String.class, args, getName());

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
          converter.toJavaList(String.class, arg1, 1, "eventNames", getName());
      LuaFunction eventHandler =
          converter.toJava(LuaFunction.class, arg2, 2, "eventHandler", getName());

      EventSubscription subscription = delegate.subscribe(eventNames, eventHandler);
      Object result = converter.toLua(subscription);
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
      Collection<String> eventNames = converter.toJavaList(String.class, args, getName());
      EventQueue eventQueue = delegate.connect(eventNames);
      Object result = converter.toLua(eventQueue);
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
      String eventName = converter.toJava(String.class, arg1, 1, "eventName", getName());
      delegate.fire(eventName, arg2);
      context.getReturnBuffer().setTo();
    }
  }
}
