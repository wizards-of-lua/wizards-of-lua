package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.Metatables;
import net.karneim.luamod.lua.wrapper.Vec3Instance;
import net.minecraft.util.math.BlockPos;
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

@TypeName("Vec3")
@ModulePackage(Constants.MODULE_PACKAGE)
public class Vec3Class extends AbstractLuaType {

  public void installInto(ChunkLoader loader, DirectCallExecutor executor, StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc = loader.loadTextChunk(new Variable(getRepo().getEnv()), getTypeName(),
        String.format("require \"%s\"", getModule()));
    executor.call(state, classFunc);
  }

  public Vec3Instance newInstance(Vec3d delegate) {
    return new Vec3Instance(getRepo(), delegate, Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  public Vec3Instance newInstance(BlockPos delegate) {
    Vec3d vec3d = new Vec3d(delegate);
    return new Vec3Instance(getRepo(), vec3d, Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  public LuaFunction FROM() {
    Table metatable = Metatables.get(getRepo().getEnv(), getTypeName());
    LuaFunction result = (LuaFunction) metatable.rawget("from");
    return result;
  }

}
