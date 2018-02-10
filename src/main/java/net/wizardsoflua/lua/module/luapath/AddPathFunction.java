package net.wizardsoflua.lua.module.luapath;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.function.NamedFunction1;

public class AddPathFunction extends NamedFunction1 {

  public static final String LUA_EXTENSION_WILDCARD = "?.lua";

  public interface Context {
    String getLuaPathElementOfPlayer(String nameOrUuid);

    void addPath(String path);
  }

  public static AddPathFunction installInto(Table env, Converters converters, Context context) {
    AddPathFunction result = new AddPathFunction(converters, context);
    env.rawset(result.getName(), result);
    return result;
  }

  private Converters converters;
  private Context context;

  public AddPathFunction(Converters converters, Context context) {
    this.converters = converters;
    this.context = context;
  }

  @Override
  public String getName() {
    return "addpath";
  }

  @Override
  public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
    String nameOrUuid = converters.toJava(String.class, arg1, 1, getName());
    String pathElement = this.context.getLuaPathElementOfPlayer(nameOrUuid);
    this.context.addPath(pathElement);
    context.getReturnBuffer().setTo();
  }
}
