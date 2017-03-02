package net.karneim.luamod.lua;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class ClassMetatables {

  private static final String CLASS = "_Class";

  public static void initClassMetatables(Table env) {
    getClassMetatables(env);
  }
  
  public static Table getMetatable(Table env, Class<?> cls) {
    Table classMetatables = getClassMetatables(env);
    String key = cls.getName();
    Table result = getClassMetatable(classMetatables, key);
    return result;
  }

  private static Table getClassMetatable(Table classMetatables, String key) {
    Table result = (Table) classMetatables.rawget(key);
    if (result == null) {
      result = new DefaultTable();
      result.rawset("__classname", key);
      classMetatables.rawset(key, result);
    }
    return result;
  }
  
  private static Table getClassMetatables(Table env) {
    Table result = (Table) env.rawget(CLASS);
    if (result == null) {
      result = new DefaultTable();
      env.rawset(CLASS, result);
      Table metatable = new DefaultTable();
      metatable.rawset("__index", metatable);
      metatable.rawset("metatable", new MetatableFunction(result));
      result.setMetatable(metatable);
    }
    return result;
  }

  private static class MetatableFunction extends AbstractFunction1 {
    private final Table table;

    public MetatableFunction(Table table) {
      this.table = table;
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      Object result = getClassMetatable(table, String.valueOf(arg1));
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }

  }
}
