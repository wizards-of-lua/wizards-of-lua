package net.karneim.luamod.cursor;

import net.minecraft.util.math.Vec3d;

public class Location {
  private final Vec3d position;
  private final float rotation;

  public Location(Vec3d position, float rotation) {
    this.position = position;
    this.rotation = rotation;
  }

  public Vec3d getPosition() {
    return position;
  }

  public float getRotation() {
    return rotation;
  }

}
