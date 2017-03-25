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

public class Vec3Instance extends ImmutableTableWrapper<Vec3d> {

  public Vec3Instance(Table env, @Nullable Vec3d delegate, Table metatable) {
    super(env, delegate, metatable);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder builder) {
    builder.add("x", delegate.xCoord);
    builder.add("y", delegate.yCoord);
    builder.add("z", delegate.zCoord);
  }
  
}
