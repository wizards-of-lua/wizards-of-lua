package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.event.GenericLuaEventInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

public class GenericLuaEventClass {

  private final String classname = "GenericEvent";
  public final String module = "net.karneim.luamod.lua.classes." + classname;

  private static final GenericLuaEventClass SINGLETON = new GenericLuaEventClass();

  public static GenericLuaEventClass get() {
    return SINGLETON;
  }

  public void installInto(Table env, ChunkLoader loader, DirectCallExecutor executor,
      StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc =
        loader.loadTextChunk(new Variable(env), classname, String.format("require \"%s\"", module));
    executor.call(state, classFunc);
  }

  public GenericLuaEventInstance newInstance(Table env, Object delegate, String name) {
    return new GenericLuaEventInstance(env, delegate, name, Metatables.get(env, classname));
  }

}
