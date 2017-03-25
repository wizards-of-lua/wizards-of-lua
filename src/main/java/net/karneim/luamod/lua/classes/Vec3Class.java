package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.Metatables;
import net.karneim.luamod.lua.wrapper.Vec3Instance;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

public class Vec3Class {

  private final String classname = "Vec3";
  public final String module = "net.karneim.luamod.lua.classes." + classname;

  private static final Vec3Class SINGLETON = new Vec3Class();

  public static Vec3Class get() {
    return SINGLETON;
  }

  public void installInto(Table env, ChunkLoader loader, DirectCallExecutor executor,
      StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc =
        loader.loadTextChunk(new Variable(env), classname, String.format("require \"%s\"", module));
    executor.call(state, classFunc);
  }

  public Vec3Instance newInstance(Table env, Vec3d delegate) {
    return new Vec3Instance(env, delegate, Metatables.get(env, classname));
  }
  
  public LuaFunction FROM(Table env) {
    Table metatable = Metatables.get(env, classname);
    LuaFunction result = (LuaFunction) metatable.rawget("from");
    return result;
  }

}
