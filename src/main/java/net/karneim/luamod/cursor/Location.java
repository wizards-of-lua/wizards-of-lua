package net.karneim.luamod.cursor;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public class Location {
  private final BlockPos position;
  private final Rotation rotation;

  public Location(BlockPos position, Rotation rotation) {
    this.position = position;
    this.rotation = rotation;
  }

  public BlockPos getPosition() {
    return position;
  }

  public Rotation getRotation() {
    return rotation;
  }

}
