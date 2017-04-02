package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableTableWrapper;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Table;

public class Vec3Instance extends ImmutableTableWrapper<Vec3d> {

  public Vec3Instance(LuaTypesRepo repo, @Nullable Vec3d delegate, Table metatable) {
    super(repo, delegate, metatable);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder builder) {
    builder.add("x", delegate.xCoord);
    builder.add("y", delegate.yCoord);
    builder.add("z", delegate.zCoord);
  }

}
