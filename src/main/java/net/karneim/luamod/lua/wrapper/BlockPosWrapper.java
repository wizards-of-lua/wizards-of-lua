package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.sandius.rembulan.LuaObject;
import net.sandius.rembulan.impl.DefaultTable;

public class BlockPosWrapper extends LuaWrapper<BlockPos> {
  public BlockPosWrapper(@Nullable BlockPos delegate) {
    super(delegate);
  }

  @Override
  protected LuaObject toLuaObject() {
    DefaultTable result = new DefaultTable();
    result.rawset("x", delegate.getX());
    result.rawset("y", delegate.getY());
    result.rawset("z", delegate.getZ());
    return result;
  }
}
