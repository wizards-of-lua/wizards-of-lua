package net.wizardsoflua.lua.module.system;

import java.util.Collection;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
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
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      Collection<String> command = getConverters().toJavaCollection(String.class, args, getName());
      ExecutionResult result = delegate.execute(command);
      context.getReturnBuffer().setTo(result.exitValue, result.response);
    }

    @Override
    public String getName() {
      return "execute";
    }
  }

}
