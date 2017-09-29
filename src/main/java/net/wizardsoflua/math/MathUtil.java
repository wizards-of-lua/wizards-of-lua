package net.wizardsoflua.math;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MathUtil {

  public static final Vec3d getVectorForRotation(float rotationYaw) {
    rotationYaw = MathHelper.wrapDegrees(rotationYaw);
    if (rotationYaw == 0) {
      return new Vec3d(0, 0, 1);
    }
    if (rotationYaw == 90) {
      return new Vec3d(-1, 0, 0);
    }
    if (rotationYaw == -180) {
      return new Vec3d(0, 0, -1);
    }
    if (rotationYaw == -90) {
      return new Vec3d(1, 0, 0);
    }
    float z = MathHelper.cos(-rotationYaw * 0.017453292F - (float) Math.PI);
    float x = MathHelper.sin(-rotationYaw * 0.017453292F - (float) Math.PI);
    return new Vec3d(-x, 0, -z);
  }
}
