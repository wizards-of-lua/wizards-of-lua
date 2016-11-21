package net.karneim.luamod.lua.event;

import java.util.Collection;

import net.karneim.luamod.lua.SleepActivator;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction0;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;

public class EventListenerWrapper {

  private final Collection<? extends EventListener> listeners;
  private final SleepActivator sleepActivator;
  private final Table luaTable = new DefaultTable();

  public EventListenerWrapper(Collection<? extends EventListener> listeners,
      SleepActivator sleepActivator) {
    this.listeners = listeners;
    this.sleepActivator = sleepActivator;
    luaTable.rawset("deregister", new DeregisterFunction());
    luaTable.rawset("next", new NextFunction());
  }

  public Table getLuaTable() {
    return luaTable;
  }

  private class DeregisterFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      for (EventListener listener : listeners) {
        listener.clear();
        sleepActivator.removeEventListener(listener);
      }
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class NextFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      // System.out.println("next: " + arg1);
      if (arg1 != null && !(arg1 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("Integer value expected but got %s!", arg1));
      }
      int ticks = arg1 == null ? Integer.MAX_VALUE : ((Number) arg1).intValue();

      sleepActivator.waitForEvents(listeners, ticks);
      execute(context);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      execute(context);
    }

    private void execute(ExecutionContext context) throws ResolvedControlThrowable {
      try {
        context.pauseIfRequested();
      } catch (UnresolvedControlThrowable e) {
        throw e.resolve(NextFunction.this, "Waiting for event");
      }
      for (EventListener listener : listeners) {
        if (listener.hasNext()) {
          sleepActivator.stopWaitingForEvent();
          context.getReturnBuffer().setTo(listener.next().getLuaObject());
          return;
        }
      }
    }

  }

}
