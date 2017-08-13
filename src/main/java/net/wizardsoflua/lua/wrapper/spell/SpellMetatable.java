package net.wizardsoflua.lua.wrapper.spell;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.lib.StringLib;
import net.sandius.rembulan.runtime.AbstractFunctionAnyArg;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.wrapper.WrapperFactory;

public class SpellMetatable {
  private class ExecuteFunction extends AbstractFunctionAnyArg {
    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      Object arg0 = args[0];
      wrappers.getTypes().checkAssignable(SpellWrapper.METATABLE_NAME, arg0);
      SpellWrapper wrapper = (SpellWrapper)arg0;
      

      LuaFunction formatFunc = StringLib.format();
      Object[] argArray = new Object[args.length - 1];
      System.arraycopy(args, 1, argArray, 0, args.length - 1);
      formatFunc.invoke(context, argArray);
      String command = String.valueOf(context.getReturnBuffer().get(0));

      int result = wrapper.execute(command);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
  
  private final WrapperFactory wrappers;
  
  public SpellMetatable(WrapperFactory wrappers, Table metatable) {
    this.wrappers = wrappers;
    metatable.rawset("execute", new ExecuteFunction());
  }
}
