package net.wizardsoflua.lua.module.types;

import static com.google.common.base.Preconditions.checkNotNull;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.Converters;

public class TypesModule {
  public static TypesModule installInto(Table env, Types types, Converters converters) {
    TypesModule result = new TypesModule(types, converters);
    env.rawset("Types", result.getLuaTable());
    return result;
  }

  private final Converters converters;
  private final Types types;
  private final Table luaTable = DefaultTable.factory().newTable();

  public TypesModule(Types types, Converters converters) {
    this.converters = checkNotNull(converters, "converters==null!");
    this.types = types;
    luaTable.rawset("declare", new DeclareFunction());
    luaTable.rawset("instanceOf", new InstanceOfFunction());
    luaTable.rawset("getTypename", new GetTypenameFunction());
  }

  public Table getLuaTable() {
    return luaTable;
  }

  private class DeclareFunction extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      String luaClassName = converters.toJava(String.class, arg1);
      Table superClassMetaTable = converters.toJavaNullable(Table.class, arg2);

      types.declareClass(luaClassName, superClassMetaTable);

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
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      Table classMT = converters.castToTableNullable(arg1);
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
