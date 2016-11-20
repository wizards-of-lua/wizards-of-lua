package net.karneim.luamod.cursor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class IncludeMask implements BlockMask {

  private final List<Block> includedBlocks = new ArrayList<Block>();

  public IncludeMask(Block... blocksToInclude) {
    for (Block block : blocksToInclude) {
      this.includedBlocks.add(block);
    }
  }

  @Override
  public boolean accepts(BlockPos pos, IBlockState blkState) {
    for (Block block : includedBlocks) {
      if (Block.isEqualTo(blkState.getBlock(), block)) {
        return true;
      }
    }
    return false;
  }

}
