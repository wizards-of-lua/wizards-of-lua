package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.LuaVec3;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableTableWrapper;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;

public class Vec3dWrapper extends ImmutableTableWrapper<Vec3d> {

  public Vec3dWrapper(Table env, @Nullable Vec3d delegate) {
    super(env, delegate);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder builder) {
    builder.add("x", delegate.xCoord);
    builder.add("y", delegate.yCoord);
    builder.add("z", delegate.zCoord);
    Table metatable = LuaVec3.META_TABLE(env);
    builder.setMetatable(metatable);
  }
}
