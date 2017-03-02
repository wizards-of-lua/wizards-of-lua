package net.karneim.luamod.cursor;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.Vec3d;

public class Location {
  private final Vec3d position;
  private final Rotation rotation;

  public Location(Vec3d position, Rotation rotation) {
    this.position = position;
    this.rotation = rotation;
  }

  public Vec3d getPosition() {
    return position;
  }

  public Rotation getRotation() {
    return rotation;
  }

}
