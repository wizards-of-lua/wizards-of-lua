package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.LuaObject;
import net.sandius.rembulan.impl.DefaultTable;

public class Vec3dWrapper extends LuaWrapper<Vec3d> {
  public Vec3dWrapper(@Nullable Vec3d delegate) {
    super(delegate);
  }

  @Override
  protected LuaObject toLuaObject() {
    DefaultTable result = new DefaultTable();
    result.rawset("x", delegate.xCoord);
    result.rawset("y", delegate.yCoord);
    result.rawset("z", delegate.zCoord);
    return result;
  }
}
