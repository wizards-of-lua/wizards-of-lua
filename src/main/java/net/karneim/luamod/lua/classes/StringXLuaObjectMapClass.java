package net.karneim.luamod.lua.classes;

import java.util.Map;

import net.karneim.luamod.lua.wrapper.Metatables;
import net.karneim.luamod.lua.wrapper.StringXLuaObjectMapInstance;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

public class StringXLuaObjectMapClass {
  // TODO we don't need a metatable for this
  private final String classname = "StringXLuaObjectMap";
  public final String module = "net.karneim.luamod.lua.classes." + classname;

  private static final StringXLuaObjectMapClass SINGLETON = new StringXLuaObjectMapClass();

  public static StringXLuaObjectMapClass get() {
    return SINGLETON;
  }

  public void installInto(Table env, ChunkLoader loader, DirectCallExecutor executor,
      StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc =
        loader.loadTextChunk(new Variable(env), classname, String.format("require \"%s\"", module));
    executor.call(state, classFunc);
  }

  public StringXLuaObjectMapInstance newInstance(Table env, Map<String, Object> delegate) {
    return new StringXLuaObjectMapInstance(env, delegate, Metatables.get(env, classname));
  }

}
