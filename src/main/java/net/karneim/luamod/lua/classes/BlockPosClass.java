package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.BlockPosInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraft.util.math.BlockPos;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

public class BlockPosClass {

  // TODO do we need this? can we use Vec3?
  private final String classname = "BlockPos";
  public final String module = "net.karneim.luamod.lua.classes." + classname;

  private static final BlockPosClass SINGLETON = new BlockPosClass();

  public static BlockPosClass get() {
    return SINGLETON;
  }

  public void installInto(Table env, ChunkLoader loader, DirectCallExecutor executor,
      StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc =
        loader.loadTextChunk(new Variable(env), classname, String.format("require \"%s\"", module));
    executor.call(state, classFunc);
  }

  public BlockPosInstance newInstance(Table env, BlockPos delegate) {
    return new BlockPosInstance(env, delegate, Metatables.get(env, classname));
  }

}
