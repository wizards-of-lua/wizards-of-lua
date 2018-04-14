package net.wizardsoflua.lua.module.types;

import com.google.auto.service.AutoService;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.TableFactory;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.extension.api.inject.AfterInjection;
import net.wizardsoflua.lua.extension.api.inject.Inject;
import net.wizardsoflua.lua.extension.api.service.Converter;
import net.wizardsoflua.lua.extension.api.service.LuaClassLoader;
import net.wizardsoflua.lua.extension.spi.LuaExtension;
import net.wizardsoflua.lua.extension.util.AbstractLuaModule;
import net.wizardsoflua.lua.function.NamedFunction1;
import net.wizardsoflua.lua.function.NamedFunction2;

@AutoService(LuaExtension.class)
public class TypesModule extends AbstractLuaModule {
  @Inject
  private TableFactory tableFactory;
  @Inject
  private Converter converter;
  @Inject
  private Table env;
  @Inject
  private LuaClassLoader classLoader;

  private Types delegate;

  public TypesModule() {
    add(new DeclareFunction());
    add(new InstanceOfFunction());
    add(new GetTypenameFunction());
  }

  @AfterInjection
  public void initialize() {
    delegate = new Types(env, classLoader);
  }

  @Override
  public String getName() {
    return "Types";
  }

  @Override
  public Table createTable() {
    return tableFactory.newTable();
  }

  public Types getDelegate() {
    return delegate;
  }

  private class DeclareFunction extends NamedFunction2 {
    @Override
    public String getName() {
      return "declare";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      String className = converter.toJava(String.class, arg1, 1, "className", getName());
      Table metatable = converter.toJavaNullable(Table.class, arg2, 2, "metatable", getName());
      delegate.declareClass(className, metatable);
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
      Table classMetaTable = converter.toJavaNullable(Table.class, arg1, 1, "class", getName());
      boolean result = delegate.isInstanceOf(classMetaTable, arg2);
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
      String result = delegate.getTypename(arg1);
      context.getReturnBuffer().setTo(result);
    }
  }
}
