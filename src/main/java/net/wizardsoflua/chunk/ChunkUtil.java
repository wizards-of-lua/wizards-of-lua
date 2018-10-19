package net.wizardsoflua.chunk;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class ChunkUtil {

  /**
   * The 'contains' function calculates whether the block with the given BlockPos is part of the
   * world chunk with the given ChunkPos.
   */
  public static boolean contains(ChunkPos cPos, BlockPos blockPos) {
    int chunkX = blockPos.getX() >> 4;
    int chunkZ = blockPos.getZ() >> 4;
    return cPos.x == chunkX && cPos.z == chunkZ;
  }
}
