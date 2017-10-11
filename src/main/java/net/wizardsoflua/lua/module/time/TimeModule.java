package net.wizardsoflua.lua.module.time;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;

public class TimeModule extends DelegatingProxy {

  public static TimeModule installInto(Converters converters, Time runtime) {
    TimeModule result = new TimeModule(converters, runtime);
    Table env = converters.getTypes().getEnv();
    env.rawset("Time", result);
    return result;
  }

  private final Time delegate;

  public TimeModule(Converters converters, Time delegate) {
    super(converters, null, delegate);
    this.delegate = delegate;
    add("autosleep", () -> delegate.isAutoSleep(), this::setAutoSleep);
    addReadOnly("allowance", () -> delegate.getAllowance());
    addReadOnly("luatime", () -> delegate.getLuaTicks());
    addReadOnly("gametime", () -> delegate.getGameTotalTime());
    addReadOnly("realtime", () -> delegate.getRealtime());

    addImmutable("sleep", new SleepFunction());
    addImmutable("getDate", new GetDateFunction());
  }

  public void setAutoSleep(Object luaObj) {
    boolean value = getConverters().toJava(Boolean.class, luaObj);
    delegate.setAutoSleep(value);
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
      try {
        context.pauseIfRequested();
      } catch (UnresolvedControlThrowable e) {
        throw e.resolve(SleepFunction.this, "Sleeping");
      }

      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      try {
        context.pauseIfRequested();
      } catch (UnresolvedControlThrowable e) {
        throw e.resolve(SleepFunction.this, "Sleeping");
      }
      context.getReturnBuffer().setTo();
    }
  }

}
