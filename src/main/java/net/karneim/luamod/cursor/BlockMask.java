package net.karneim.luamod.cursor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public interface BlockMask {
  boolean accepts(BlockPos pos, IBlockState blkState);
}