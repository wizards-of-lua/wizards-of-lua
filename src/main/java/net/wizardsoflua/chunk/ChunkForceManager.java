package net.wizardsoflua.chunk;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.wizardsoflua.ServerScoped;

/**
 * Intermediate adaptor for vanilla's chunk force loading system until Forge comes up with
 * something.
 *
 * @see https://github.com/MinecraftForge/MinecraftForge/issues/5487
 */
@ServerScoped
public class ChunkForceManager {

  private class WorldManager {
    private final World world;

    public WorldManager(World world) {
      this.world = checkNotNull(world, "world == null!");
    }

    private final Map<ChunkPos, ChunkForce> chunkForces = new HashMap<>();

    public ChunkForce getChunkForce(ChunkPos chunkPos) {
      return chunkForces.computeIfAbsent(chunkPos, key -> new ChunkForce(world, chunkPos));
    }
  }

  private final Map<World, WorldManager> managers = new HashMap<>();

  public void forceChunk(Ticket ticket, ChunkPos chunkPos) {
    WorldManager m = getManager(ticket.getWorld());
    ChunkForce chunkForce = m.getChunkForce(chunkPos);
    if (chunkForce.add(ticket)) {
      ticket.add(chunkForce);
    }
  }

  public void unforceChunk(Ticket ticket, ChunkPos chunkPos) {
    WorldManager m = getManager(ticket.getWorld());
    ChunkForce chunkForce = m.getChunkForce(chunkPos);
    if (chunkForce.remove(ticket)) {
      ticket.remove(chunkForce);
    }
  }

  private WorldManager getManager(World world) {
    return managers.computeIfAbsent(world, key -> new WorldManager(world));
  }

}
