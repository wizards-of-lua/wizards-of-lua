package net.wizardsoflua.spell;

import java.util.List;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

/**
 * The {@link VirtualEntityChunkLoaderSupport} ensures that the chunk at the {@link VirtualEntity}'s
 * position will stay loaded continuously.
 */
public class VirtualEntityChunkLoaderSupport {

  public static void enableTicketSupport(Object mod) {
    ForgeChunkManager.setForcedChunkLoadingCallback(mod,
        new net.minecraftforge.common.ForgeChunkManager.LoadingCallback() {
          @Override
          public void ticketsLoaded(List<Ticket> tickets, World world) {
            // This is called when the server is restarted and if there are tickets that we
            // have registered before.
            // Since we do not support to restore interrupted Lua programs, we do not need
            // to do here anything.
          }
        });
  }

  private final Object mod;
  private final VirtualEntity entity;
  private Ticket chunkLoaderTicket;
  private ChunkPos chunkPos;

  public VirtualEntityChunkLoaderSupport(Object mod, VirtualEntity entity) {
    this.mod = mod;
    this.entity = entity;
    this.chunkPos = new ChunkPos(entity.getPosition());
    loadChunk(chunkPos);
    loadChunkProximity(chunkPos);
  }

  public void requestNewTicket() {
    if (hasTicket()) {
      releaseTicket();
    }
    chunkPos = new ChunkPos(entity.getPosition());
    chunkLoaderTicket = ForgeChunkManager.requestTicket(mod, entity.getEntityWorld(),
        ForgeChunkManager.Type.NORMAL);
    if (chunkLoaderTicket == null) {
      throw new IllegalStateException("Could not get a ChunkLoading ticket for Wizards of Lua!");
    }
    loadChunkProximity(chunkPos);
    ForgeChunkManager.forceChunk(chunkLoaderTicket, chunkPos);
  }

  public boolean hasTicket() {
    return chunkLoaderTicket != null;
  }

  public void releaseTicket() {
    if (hasTicket()) {
      try {
        ForgeChunkManager.releaseTicket(chunkLoaderTicket);
      } catch (Throwable e) {
        // ignored
      }
      chunkLoaderTicket = null;
    }
  }

  public void updatePosition() {
    BlockPos pos = entity.getPosition();
    // TODO probably better use "contains" instead of "isInside"
    if (!isInside(chunkPos, pos)) {
      if (chunkLoaderTicket != null) {
        ForgeChunkManager.unforceChunk(chunkLoaderTicket, chunkPos);
        chunkPos = new ChunkPos(pos);
        loadChunkProximity(chunkPos);
        ForgeChunkManager.forceChunk(chunkLoaderTicket, chunkPos);
      } else {
        chunkPos = new ChunkPos(pos);
        loadChunk(chunkPos);
        loadChunkProximity(chunkPos);
      }
    }
  }

  private boolean isInside(ChunkPos cPos, BlockPos pos) {
    int xmin = cPos.getXStart();
    int xmax = cPos.getXEnd();
    int zmin = cPos.getZStart();
    int zmax = cPos.getZEnd();
    boolean result =
        xmin <= pos.getX() && pos.getX() <= xmax && zmin <= pos.getZ() && pos.getZ() <= zmax;
    return result;
  }

  private void loadChunk(ChunkPos chunkPos) {
    entity.getEntityWorld().getChunkFromChunkCoords(chunkPos.x, chunkPos.z);
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
          entity.getEntityWorld().getChunkFromChunkCoords(center.x + x, center.z + z);
        }
      }
    }
  }

}
