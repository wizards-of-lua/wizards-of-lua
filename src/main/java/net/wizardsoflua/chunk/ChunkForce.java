package net.wizardsoflua.chunk;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

class ChunkForce {

  private final World world;
  private final ChunkPos chunkPos;
  private final Set<Ticket> tickets = new HashSet<>();
  private boolean isActive = false;

  public ChunkForce(World world, ChunkPos chunkPos) {
    this.world = checkNotNull(world, "world == null!");
    this.chunkPos = checkNotNull(chunkPos, "chunkPos == null!");
  }

  public boolean add(Ticket ticket) {
    if (tickets.add(ticket)) {
      if (!isActive) {
        isActive = true;
        setChunkForced(world, chunkPos.x, chunkPos.z, true);
      }
      return true;
    }
    return false;
  }

  public boolean remove(Ticket ticket) {
    if (tickets.remove(ticket)) {
      if (isActive && tickets.isEmpty()) {
        isActive = false;
        setChunkForced(world, chunkPos.x, chunkPos.z, false);
      }
      return true;
    }
    return false;
  }

  private boolean setChunkForced(World world, int chunkX, int chunkZ, boolean forced) {
    return world.func_212414_b(chunkX, chunkZ, forced);
  }

}
