package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.impl.ImmutableTable;

public class Vec3dWrapper extends StructuredLuaWrapper<Vec3d> {
  public Vec3dWrapper(@Nullable Vec3d delegate) {
    super(delegate);
  }

  @Override
  protected void addProperties(ImmutableTable.Builder builder) {
    super.addProperties(builder);
    builder.add("x", delegate.xCoord);
    builder.add("y", delegate.yCoord);
    builder.add("z", delegate.zCoord);
  }

}
