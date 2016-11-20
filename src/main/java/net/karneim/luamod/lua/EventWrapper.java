package net.karneim.luamod.lua;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.event.Event;
import net.karneim.luamod.lua.event.EventListener;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction0;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;

public class EventWrapper {

  private final EventListener listener;
  private final SleepActivator sleepActivator;
  private final Table luaTable = new DefaultTable();

  public EventWrapper(EventListener listener, SleepActivator sleepActivator) {
    this.listener = listener;
    this.sleepActivator = sleepActivator;
    luaTable.rawset("deregister", new DeregisterFunction());
    luaTable.rawset("next", new NextFunction());
    sleepActivator.addEventListener(listener);
  }

  public Table getLuaTable() {
    return luaTable;
  }

  private class DeregisterFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      // System.out.println("unregister");
      sleepActivator.removeEventListener(listener);
      listener.clear();
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

      sleepActivator.waitForEvent(listener, ticks);
      try {
        context.pauseIfRequested();
      } catch (UnresolvedControlThrowable e) {
        throw e.resolve(NextFunction.this, "Waiting for event");
      }
      if (listener.hasNext()) {
        sleepActivator.stopWaitingForEvent();
      }
      context.getReturnBuffer().setTo(unwrapPayload(listener.next()));
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      try {
        context.pauseIfRequested();
      } catch (UnresolvedControlThrowable e) {
        e.resolve(NextFunction.this, "Waiting for event");
      }
      if (listener.hasNext()) {
        sleepActivator.stopWaitingForEvent();
      }
      context.getReturnBuffer().setTo(unwrapPayload(listener.next()));
    }

    private @Nullable Object unwrapPayload(@Nullable Event event) {
      if (event != null) {
        return event.getPayload();
      }
      return null;
    }
  }

}
