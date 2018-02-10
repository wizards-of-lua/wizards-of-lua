package net.wizardsoflua.lua.module.types;

import static com.google.common.base.Preconditions.checkNotNull;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.function.NamedFunction1;
import net.wizardsoflua.lua.function.NamedFunction2;

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
    DeclareFunction declareFunction = new DeclareFunction();
    luaTable.rawset(declareFunction.getName(), declareFunction);
    InstanceOfFunction instanceOfFunction = new InstanceOfFunction();
    luaTable.rawset(instanceOfFunction.getName(), instanceOfFunction);
    GetTypenameFunction getTypenameFunction = new GetTypenameFunction();
    luaTable.rawset(getTypenameFunction.getName(), getTypenameFunction);
  }

  public Table getLuaTable() {
    return luaTable;
  }

  private class DeclareFunction extends NamedFunction2 {
    @Override
    public String getName() {
      return "declare";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      String className = converters.toJava(String.class, arg1, 1, "className", getName());
      Table metatable = converters.toJavaNullable(Table.class, arg2, 2, "metatable", getName());
      types.declareClass(className, metatable);
      context.getReturnBuffer().setTo();
    }
  }

  private class InstanceOfFunction extends NamedFunction2 {
    @Override
    public String getName() {
      return "instanceOf";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      Table classMetaTable = converters.toJavaNullable(Table.class, arg1, 1, "class", getName());
      boolean result = types.isInstanceOf(classMetaTable, arg2);
      context.getReturnBuffer().setTo(result);
    }
  }

  private class GetTypenameFunction extends NamedFunction1 {
    @Override
    public String getName() {
      return "getTypename";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      String result = types.getTypename(arg1);
      context.getReturnBuffer().setTo(result);
    }
  }
}
