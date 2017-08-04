package net.wizardsoflua.lua.runtime;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction0;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;

public class RuntimeModule {

  public static RuntimeModule installInto(Table env, Runtime runtime) {
    RuntimeModule result = new RuntimeModule(runtime);
    env.rawset("Runtime", result.getLuaTable());
    return result;
  }

  private final Runtime runtime;
  private final Table luaTable = DefaultTable.factory().newTable();

  public RuntimeModule(Runtime runtime) {
    this.runtime = runtime;
    luaTable.rawset("sleep", new SleepFunction());
    luaTable.rawset("getAllowance", new GetAllowanceFunction());
    luaTable.rawset("getRealtime", new GetRealtimeFunction());
    luaTable.rawset("getGametime", new GetGametimeFunction());
    luaTable.rawset("getLuatime", new GetLuatimeFunction());
    luaTable.rawset("getRealDateTime", new GetRealDateTimeFunction());
    luaTable.rawset("setAutoSleep", new SetAutoSleepFunction());
  }

  public Table getLuaTable() {
    return luaTable;
  }

  private class GetLuatimeFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      long result = runtime.getLuaTicks();
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetGametimeFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      long result = runtime.getGameTotalTime();
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetAllowanceFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      long result = runtime.getAllowance();
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetRealtimeFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      long result = runtime.getRealtime();
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetRealDateTimeFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      String pattern = arg1 == null ? null : String.valueOf(arg1);
      String result = runtime.getRealDateTime(pattern);
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

      runtime.startSleep(ticks);
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

  private class SetAutoSleepFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (!(arg1 instanceof Boolean)) {
        throw new IllegalArgumentException(
            String.format("Boolean value expected but got %s!", arg1));
      }
      boolean autoSleep = ((Boolean) arg1).booleanValue();
      runtime.setAutoSleep(autoSleep);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
}
