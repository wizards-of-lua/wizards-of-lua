package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.LuaObject;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;

public class Vec3dWrapper extends LuaWrapper<Vec3d> {
  public Vec3dWrapper(@Nullable Vec3d delegate) {
    super(delegate);
  }

  @Override
  protected LuaObject toLuaObject() {
    Table result = DefaultTable.factory().newTable();
    result.rawset("x", delegate.xCoord);
    result.rawset("y", delegate.yCoord);
    result.rawset("z", delegate.zCoord);
    return result;
  }
}
