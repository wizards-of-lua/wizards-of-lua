package net.wizardsoflua.lua.module.luapath;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.module.types.Terms;

public class AddPathFunction extends AbstractFunction1 {

  public static final String LUA_EXTENSION_WILDCARD = "?.lua";

  public interface Context {
    String getLuaPathElementOfPlayer(String nameOrUuid);
  }

  public static AddPathFunction installInto(Table env, Converters converters, Context context) {
    AddPathFunction result = new AddPathFunction(env, converters, context);
    env.rawset("addpath", result);
    return result;
  }

  private Table env;
  private Converters converters;
  private Context context;

  public AddPathFunction(Table env, Converters converters, Context context) {
    this.env = env;
    this.converters = converters;
    this.context = context;
  }

  @Override
  public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
    String nameOrUuid = converters.getTypes().castString(arg1, Terms.MANDATORY);
    String pathElement = this.context.getLuaPathElementOfPlayer(nameOrUuid);

    Table pkg = (Table) env.rawget("package");
    String path = converters.getTypes().castString(pkg.rawget("path"), Terms.MANDATORY);

    if (!path.contains(pathElement)) {
      path += ";" + pathElement;
      pkg.rawset("path", path);
    }

    context.getReturnBuffer().setTo();
  }

  @Override
  public void resume(ExecutionContext context, Object suspendedState)
      throws ResolvedControlThrowable {
    throw new NonsuspendableFunctionException();
  }

}
