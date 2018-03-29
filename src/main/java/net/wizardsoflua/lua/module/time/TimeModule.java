package net.wizardsoflua.lua.module.time;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;

public class TimeModule extends DelegatingProxy<Time> {
  public static TimeModule installInto(Table env, LuaClassLoader classLoader, Time time) {
    TimeModule result = new TimeModule(classLoader, time);
    env.rawset("Time", result);
    return result;
  }

  public TimeModule(LuaClassLoader classLoader, Time delegate) {
    super(classLoader, null, delegate);
    addReadOnly("allowance", () -> delegate.getAllowance());
    add("autosleep", () -> delegate.isAutosleep(), this::setAutosleep);
    addReadOnly("luatime", () -> delegate.getLuaTicks());
    addReadOnly("gametime", () -> delegate.getGameTotalTime());
    addReadOnly("realtime", () -> delegate.getRealtime());

    addImmutable("sleep", new SleepFunction());
    addImmutable("getDate", new GetDateFunction());
  }

  @Override
  public boolean isTransferable() {
    return false;
  }

  public void setAutosleep(Object luaObj) {
    boolean value = getConverters().toJava(Boolean.class, luaObj, "autosleep");
    delegate.setAutosleep(value);
  }

  private class GetDateFunction extends AbstractFunction1 {
    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      String pattern = arg1 == null ? null : String.valueOf(arg1);
      String result = delegate.getDate(pattern);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class SleepFunction extends AbstractFunction1 {
    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      // System.out.println("sleep: " + arg1);
      if (arg1 == null) {
        // ignore call
        return;
      }
      if (!(arg1 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("Integer value expected but got %s!", arg1));
      }
      int ticks = ((Number) arg1).intValue();

      delegate.startSleep(ticks);
      execute(context);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      execute(context);
    }

    private void execute(ExecutionContext context) throws ResolvedControlThrowable {
      try {
        getClassLoader().getCurrentSchedulingContext().pauseIfRequested(context);
      } catch (UnresolvedControlThrowable e) {
        throw e.resolve(SleepFunction.this, null);
      }
      context.getReturnBuffer().setTo();
    }
  }
}
