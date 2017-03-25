package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.MaterialInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraft.block.material.Material;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

public class MaterialClass {

  private final String classname = "Material";
  public final String module = "net.karneim.luamod.lua.classes." + classname;

  private static final MaterialClass SINGLETON = new MaterialClass();

  public static MaterialClass get() {
    return SINGLETON;
  }

  public void installInto(Table env, ChunkLoader loader, DirectCallExecutor executor,
      StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc =
        loader.loadTextChunk(new Variable(env), classname, String.format("require \"%s\"", module));
    executor.call(state, classFunc);
  }

  public MaterialInstance newInstance(Table env, Material delegate) {
    return new MaterialInstance(env, delegate, Metatables.get(env, classname));
  }

}
