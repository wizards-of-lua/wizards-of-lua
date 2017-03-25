package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.EnumInstance;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

public class EnumClass {

  // No metatable needed for this
  private final String classname = "Enum";
  public final String module = "net.karneim.luamod.lua.classes." + classname;

  private static final EnumClass SINGLETON = new EnumClass();

  public static EnumClass get() {
    return SINGLETON;
  }

  public void installInto(Table env, ChunkLoader loader, DirectCallExecutor executor,
      StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc =
        loader.loadTextChunk(new Variable(env), classname, String.format("require \"%s\"", module));
    executor.call(state, classFunc);
  }

  public EnumInstance newInstance(Table env, Enum<?> delegate) {
    return new EnumInstance(env, delegate);
  }

}
