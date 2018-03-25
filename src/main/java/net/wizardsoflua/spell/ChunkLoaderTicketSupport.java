package net.wizardsoflua.spell;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

/**
 * The {@link ChunkLoaderTicketSupport} ensures that the chunk at the entity's position will stay
 * loaded all the time.
 *
 */
public class ChunkLoaderTicketSupport {

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
  private final Entity entity;
  private Ticket chunkLoaderTicket;
  private ChunkPos chunkPos;

  public ChunkLoaderTicketSupport(Object mod, Entity entity) {
    this.mod = mod;
    this.entity = entity;
  }

  public void request() {
    chunkPos = new ChunkPos(entity.getPosition());
    chunkLoaderTicket = ForgeChunkManager.requestTicket(mod, entity.getEntityWorld(),
        ForgeChunkManager.Type.ENTITY);
    if (chunkLoaderTicket == null) {
      throw new IllegalStateException("Could not get a ChunkLoading ticket for Wizards of Lua!");
    }
    chunkLoaderTicket.bindEntity(entity);
    loadChunkProximity(chunkPos);
    ForgeChunkManager.forceChunk(chunkLoaderTicket, chunkPos);
  }

  public void release() {
    if (chunkLoaderTicket != null) {
      try {
        ForgeChunkManager.releaseTicket(chunkLoaderTicket);
      } catch (Throwable e) {
        // ignored
      }
      chunkLoaderTicket = null;
    }
  }

  /**
   * Updates the ChunkManager by pasing in the entity's current position
   */
  public void updatePosition() {
    if (chunkLoaderTicket != null) {
      BlockPos pos = entity.getPosition();
      if (!isInside(chunkPos, pos)) {
        ForgeChunkManager.unforceChunk(chunkLoaderTicket, chunkPos);
        chunkPos = new ChunkPos(pos);
        loadChunkProximity(chunkPos);
        ForgeChunkManager.forceChunk(chunkLoaderTicket, chunkPos);
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

  /**
   * This loads the neighborhood of the chunk at the given position. This ensures that the
   * decoration elements of the world generation process are added to the chunk in the center.
   *
   * @param center
   * @see https://www.reddit.com/r/feedthebeast/comments/5x0twz/investigating_extreme_worldgen_lag/
   */
  private void loadChunkProximity(ChunkPos center) {
    IChunkProvider p = entity.getEntityWorld().getChunkProvider();
    loadChunk(p, new ChunkPos(center.x + 1, center.z + 1));
    loadChunk(p, new ChunkPos(center.x + 1, center.z));
    loadChunk(p, new ChunkPos(center.x, center.z + 1));
    loadChunk(p, new ChunkPos(center.x - 1, center.z + 1));
    loadChunk(p, new ChunkPos(center.x + 1, center.z - 1));
    loadChunk(p, new ChunkPos(center.x, center.z - 1));
    loadChunk(p, new ChunkPos(center.x - 1, center.z - 1));
    loadChunk(p, new ChunkPos(center.x - 1, center.z));
  }

  private void loadChunk(IChunkProvider p, ChunkPos pos) {
    int x = chunkPos.x;
    int z = chunkPos.z;
    if (p.getLoadedChunk(x, z) == null) {
      p.provideChunk(x, z);
    }
  }
}
