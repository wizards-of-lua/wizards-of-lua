package net.karneim.luamod.lua.wrapper;

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
    env.rawset("runtime", result.getLuaTable());
    return result;
  }

  private final Runtime runtime;
  private final Table luaTable = DefaultTable.factory().newTable();

  public RuntimeWrapper(Runtime runtime) {
    this.runtime = runtime;
    luaTable.rawset("sleep", new SleepFunction());
    luaTable.rawset("getLuaTicksTotal", new GetLuaTicksTotalFunction());
    luaTable.rawset("getAllowance", new GetAllowanceFunction());
  }

  public Table getLuaTable() {
    return luaTable;
  }

  private class GetLuaTicksTotalFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      long result = runtime.getLuaTicksTotal();
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

  private class SleepFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      // System.out.println("sleep: " + arg1);
      if (arg1 == null || !(arg1 instanceof Number)) {
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
