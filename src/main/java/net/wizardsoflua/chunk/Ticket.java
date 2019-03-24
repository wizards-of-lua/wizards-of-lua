package net.wizardsoflua.chunk;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.World;

public class Ticket {

  private final World world;
  private final Set<ChunkForce> chunkForces = new HashSet<>();

  public Ticket(World world) {
    this.world = checkNotNull(world, "world == null!");
  }

  public World getWorld() {
    return world;
  }

  void add(ChunkForce chunkForce) {
    chunkForces.add(chunkForce);
  }

  void remove(ChunkForce chunkForce) {
    chunkForces.remove(chunkForce);
  }

  public void release() {
    for (ChunkForce chunkForce : chunkForces) {
      chunkForce.remove(this);
    }
    chunkForces.clear();
  }

}
