package net.wizardsoflua.lua.module.types;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class TypesModule {
  public static TypesModule installInto(Table env, Types types) {
    TypesModule result = new TypesModule(types);
    env.rawset("Types", result.getLuaTable());
    return result;
  }

  private final Types types;
  private final Table luaTable = DefaultTable.factory().newTable();

  public TypesModule(Types Types) {
    this.types = Types;
    luaTable.rawset("declare", new DeclareFunction());
    luaTable.rawset("instanceOf", new InstanceOfFunction());
    luaTable.rawset("getTypename", new GetTypenameFunction());
  }

  public Table getLuaTable() {
    return luaTable;
  }
  
  private class DeclareFunction extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) throws ResolvedControlThrowable {
      String classname = types.castString(arg1, Terms.MANDATORY);
      Table superclassMT = types.castTable(arg2, Terms.OPTIONAL);
      
      types.declare(classname, superclassMT);
      
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
  
  private class InstanceOfFunction extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) throws ResolvedControlThrowable {
      Table classMT = types.castTable(arg1, Terms.OPTIONAL);
      boolean result = types.isInstanceOf(classMT, arg2);
      
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
  
  private class GetTypenameFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      String result = types.getTypename(arg1);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
}
