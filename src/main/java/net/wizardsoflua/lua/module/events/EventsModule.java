package net.wizardsoflua.lua.module.events;

import java.util.Collection;
import java.util.List;

import com.google.auto.service.AutoService;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.TableFactory;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.classes.eventqueue.EventQueue;
import net.wizardsoflua.lua.classes.eventsubscription.EventSubscription;
import net.wizardsoflua.lua.extension.api.inject.AfterInjection;
import net.wizardsoflua.lua.extension.api.inject.Inject;
import net.wizardsoflua.lua.extension.api.service.Converter;
import net.wizardsoflua.lua.extension.api.service.Injector;
import net.wizardsoflua.lua.extension.spi.LuaExtension;
import net.wizardsoflua.lua.extension.util.AbstractLuaModule;
import net.wizardsoflua.lua.function.NamedFunction2;
import net.wizardsoflua.lua.function.NamedFunctionAnyArg;

@AutoService(LuaExtension.class)
public class EventsModule extends AbstractLuaModule {
  @Inject
  private Converter converter;
  @Inject
  private Injector injector;
  @Inject
  private TableFactory tableFactory;

  private EventHandlers delegate;

  public EventsModule() {
    add(new SubscribeFunction());
    add(new OnFunction());
    add(new ConnectFunction());
    add(new FireFunction());
  }

  @AfterInjection
  public void initialize() {
    delegate = injector.inject(new EventHandlers());
  }

  public EventHandlers getDelegate() {
    return delegate;
  }

  @Override
  public String getName() {
    return "Events";
  }

  @Override
  public Table createTable() {
    return tableFactory.newTable();
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
