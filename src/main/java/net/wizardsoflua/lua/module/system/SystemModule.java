package net.wizardsoflua.lua.module.system;

import java.util.Collection;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.function.NamedFunctionAnyArg;

public class SystemModule extends DelegatingProxy<SystemAdapter> {
  public static SystemModule installInto(Table env, LuaClassLoader classLoader,
      SystemAdapter systemAdapter) {
    SystemModule result = new SystemModule(classLoader, systemAdapter);
    env.rawset("System", result);
    return result;
  }

  public SystemModule(LuaClassLoader classLoader, SystemAdapter delegate) {
    super(classLoader, null, delegate);
    ExecuteFunction executeFunction = new ExecuteFunction();
    addImmutable(executeFunction.getName(), executeFunction);
  }

  @Override
  public boolean isTransferable() {
    return false;
  }

  private class ExecuteFunction extends NamedFunctionAnyArg {
    @Override
    public String getName() {
      return "execute";
    }

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      Collection<String> command = getConverters().toJavaList(String.class, args, getName());
      delegate.execute(command);
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
        throw e.resolve(ExecuteFunction.this, null);
      }

      ExecutionResult result = delegate.getExecutionResult();
      context.getReturnBuffer().setTo(result.exitValue, result.response);
    }
  }

}
