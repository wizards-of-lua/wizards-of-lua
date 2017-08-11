package net.wizardsoflua.lua.compiler;

import net.sandius.rembulan.Variable;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

public class LuaFunctionBinary {

  private final Class<?> clazz;
  private final String chunkName;

  LuaFunctionBinary(Class<?> clazz, String chunkName) {
    this.clazz = clazz;
    this.chunkName = chunkName;
  }

  public LuaFunction loadInto(Variable env) throws LoaderException {
    try {
      return (LuaFunction) clazz.getConstructor(Variable.class).newInstance(env);
    } catch (RuntimeException | ReflectiveOperationException ex) {
      throw new LoaderException(ex, chunkName, 0, false);
    }
  }
}
