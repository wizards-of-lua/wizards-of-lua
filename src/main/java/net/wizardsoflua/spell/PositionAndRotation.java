package net.wizardsoflua.spell;


import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class PositionAndRotation {

  private final Vec3d pos;
  private final float rotationYaw;
  private final float rotationPitch;

  public PositionAndRotation(Vec3d pos, Vec2f yawPitch) {
    this(pos, yawPitch.x, yawPitch.y);
  }

  public PositionAndRotation(Vec3d pos, float rotationYaw, float rotationPitch) {
    this.pos = pos;
    this.rotationYaw = rotationYaw;
    this.rotationPitch = rotationPitch;
  }

  public Vec3d getPos() {
    return pos;
  }

  public float getRotationYaw() {
    return rotationYaw;
  }

  public float getRotationPitch() {
    return rotationPitch;
  }
}
