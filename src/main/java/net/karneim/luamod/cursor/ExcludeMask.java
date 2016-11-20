package net.karneim.luamod.cursor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class ExcludeMask implements BlockMask {

  private final List<Block> excludedBlocks = new ArrayList<Block>();

  public ExcludeMask(Block... blocksToExclude) {
    for (Block block : blocksToExclude) {
      this.excludedBlocks.add(block);
    }
  }

  @Override
  public boolean accepts(BlockPos pos, IBlockState blkState) {
    for (Block block : excludedBlocks) {
      if (Block.isEqualTo(blkState.getBlock(), block)) {
        return false;
      }
    }
    return true;
  }

}
