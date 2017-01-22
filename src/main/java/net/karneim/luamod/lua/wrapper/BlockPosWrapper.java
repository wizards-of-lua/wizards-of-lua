package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.DynamicTable;
import net.minecraft.util.math.BlockPos;

public class BlockPosWrapper extends StructuredLuaWrapper<BlockPos> {
  public BlockPosWrapper(@Nullable BlockPos delegate) {
    super(delegate);
  }

  @Override
  protected void addProperties(DynamicTable.Builder builder) {
    super.addProperties(builder);
    builder.add("x", delegate.getX());
    builder.add("y", delegate.getY());
    builder.add("z", delegate.getZ());
  }

}
