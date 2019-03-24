package net.wizardsoflua.spell;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.wizardsoflua.chunk.ChunkForceManager;
import net.wizardsoflua.chunk.ChunkUtil;
import net.wizardsoflua.chunk.Ticket;

/**
 * The {@link VirtualEntityChunkForceSupport} ensures that the chunk at the {@link VirtualEntity}'s
 * position will stay loaded continuously.
 */
public class VirtualEntityChunkForceSupport {

  private final ChunkForceManager support;
  private final VirtualEntity entity;
  private Ticket chunkLoaderTicket;
  private ChunkPos chunkPos;

  public VirtualEntityChunkForceSupport(ChunkForceManager support, VirtualEntity entity) {
    this.support = support;
    this.entity = entity;
    chunkPos = new ChunkPos(entity.getPosition());
    loadChunk(chunkPos);
    loadChunkProximity(chunkPos);
  }

  public void requestNewTicket() {
    if (hasTicket()) {
      releaseTicket();
    }
    chunkPos = new ChunkPos(entity.getPosition());
    chunkLoaderTicket = new Ticket(entity.getWorld());
    if (chunkLoaderTicket == null) {
      throw new IllegalStateException("Could not get a ChunkLoading ticket for Wizards of Lua!");
    }
    loadChunkProximity(chunkPos);
    support.forceChunk(chunkLoaderTicket, chunkPos);
  }

  public boolean hasTicket() {
    return chunkLoaderTicket != null;
  }

  public void releaseTicket() {
    if (hasTicket()) {
      chunkLoaderTicket.release();
      chunkLoaderTicket = null;
    }
  }

  public void updatePosition() {
    BlockPos pos = entity.getPosition();
    if (!ChunkUtil.contains(chunkPos, pos)) {
      if (chunkLoaderTicket != null) {
        support.unforceChunk(chunkLoaderTicket, chunkPos);
        chunkPos = new ChunkPos(pos);
        loadChunkProximity(chunkPos);
        support.forceChunk(chunkLoaderTicket, chunkPos);
      } else {
        chunkPos = new ChunkPos(pos);
        loadChunk(chunkPos);
        loadChunkProximity(chunkPos);
      }
    }
  }

  private void loadChunk(ChunkPos chunkPos) {
    entity.getWorld().getChunk(chunkPos.x, chunkPos.z);
  }

  /**
   * This loads the neighborhood of the chunk at the given position. This ensures that the
   * decoration elements of the world generation process are added to the chunk in the center.
   *
   * @param center
   * @see https://www.reddit.com/r/feedthebeast/comments/5x0twz/investigating_extreme_worldgen_lag/
   */
  private void loadChunkProximity(ChunkPos center) {
    for (int x = -1; x <= 1; ++x) {
      for (int z = -1; z <= 1; ++z) {
        if (!(x == 0 && z == 0)) {
          entity.getWorld().getChunk(center.x + x, center.z + z);
        }
      }
    }
  }

}
