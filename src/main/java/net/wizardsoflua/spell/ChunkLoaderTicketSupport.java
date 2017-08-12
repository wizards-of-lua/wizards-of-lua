package net.wizardsoflua.spell;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
      Vec3d pos = entity.getPositionVector();
      if (!isInside(chunkPos, pos)) {
        ForgeChunkManager.unforceChunk(chunkLoaderTicket, chunkPos);
        chunkPos = new ChunkPos(new BlockPos(pos));
        ForgeChunkManager.forceChunk(chunkLoaderTicket, chunkPos);
      }
    }
  }

  private boolean isInside(ChunkPos cPos, Vec3d pos) {
    return cPos.getXStart() <= pos.xCoord && pos.xCoord <= cPos.getXEnd()
        && cPos.getZStart() <= pos.zCoord && pos.zCoord <= cPos.getZEnd();
  }
}
