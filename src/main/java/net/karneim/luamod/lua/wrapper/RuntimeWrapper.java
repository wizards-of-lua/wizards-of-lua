package net.karneim.luamod.lua.wrapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import net.karneim.luamod.lua.Runtime;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction0;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;

public class RuntimeWrapper {

  public static RuntimeWrapper installInto(Table env, Runtime runtime) {
    RuntimeWrapper result = new RuntimeWrapper(runtime);
    env.rawset("Runtime", result.getLuaTable());
    return result;
  }

  private final Runtime runtime;
  private final Table luaTable = DefaultTable.factory().newTable();

  public RuntimeWrapper(Runtime runtime) {
    this.runtime = runtime;
    luaTable.rawset("sleep", new SleepFunction());
    luaTable.rawset("getAllowance", new GetAllowanceFunction());
    luaTable.rawset("getRealtime", new GetRealtimeFunction());
    luaTable.rawset("getGametime", new GetGametimeFunction());
    luaTable.rawset("getLuatime", new GetLuatimeFunction());
    luaTable.rawset("getRealDateTime", new GetRealDateTimeFunction());
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
      long result = System.currentTimeMillis();
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
      DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
      if (pattern != null) {
        formatter = DateTimeFormatter.ofPattern(pattern);
      }
      String result = LocalDateTime.now().format(formatter);
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
}
