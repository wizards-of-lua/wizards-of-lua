package net.karneim.luamod.cursor;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

class ScheduledBlockUpdate {

  public final BlockPos sourcePos;
  public final Block block;
  public final int delay;
  public final int priority;

  public ScheduledBlockUpdate(BlockPos sourcePos, Block block, int delay, int priority) {
    this.sourcePos = sourcePos;
    this.block = block;
    this.delay = delay;
    this.priority = priority;
  }
}