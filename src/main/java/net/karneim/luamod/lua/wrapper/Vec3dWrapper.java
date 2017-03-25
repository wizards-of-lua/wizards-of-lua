package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableTableWrapper;
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

public class Vec3dWrapper extends ImmutableTableWrapper<Vec3d> {

  private static final String CLASSNAME = "Vec3d";
  public static final String MODULE = "net.karneim.luamod.lua.classes."+CLASSNAME;

  public static void installInto(Table env, ChunkLoader loader, DirectCallExecutor executor,
      StateContext state) throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc =
        loader.loadTextChunk(new Variable(env), CLASSNAME, String.format("require \"%s\"", MODULE));
    executor.call(state, classFunc);
    //addFunctions(env);
  }
  
  public static LuaFunction FROM(Table env) {
    Table metatable = Metatables.get(env, CLASSNAME);
    LuaFunction result = (LuaFunction) metatable.rawget("from");
    return result;
  }

  public Vec3dWrapper(Table env, @Nullable Vec3d delegate) {
    super(env, delegate);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder builder) {
    builder.add("x", delegate.xCoord);
    builder.add("y", delegate.yCoord);
    builder.add("z", delegate.zCoord);
    Table metatable = Metatables.get(env, CLASSNAME);
    builder.setMetatable(metatable);
  }
  
}
