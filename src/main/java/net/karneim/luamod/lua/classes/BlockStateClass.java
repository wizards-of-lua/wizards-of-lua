package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.BlockStateInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraft.block.state.IBlockState;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

public class BlockStateClass {

  private final String classname = "BlockState";
  public final String module = "net.karneim.luamod.lua.classes." + classname;

  private static final BlockStateClass SINGLETON = new BlockStateClass();

  public static BlockStateClass get() {
    return SINGLETON;
  }

  public void installInto(Table env, ChunkLoader loader, DirectCallExecutor executor,
      StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc =
        loader.loadTextChunk(new Variable(env), classname, String.format("require \"%s\"", module));
    executor.call(state, classFunc);
  }

  public BlockStateInstance newInstance(Table env, IBlockState delegate) {
    return new BlockStateInstance(env, delegate, Metatables.get(env, classname));
  }

}
