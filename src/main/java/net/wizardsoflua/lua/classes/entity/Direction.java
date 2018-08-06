package net.wizardsoflua.lua.classes.entity;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.util.math.Vec3d;
import net.wizardsoflua.math.MathUtil;

public enum Direction {
  UP("up", new Vec3d(0, 1, 0)), //
  DOWN("down", new Vec3d(0, -1, 0)), //
  NORTH("north", new Vec3d(0, 0, -1)), //
  EAST("east", new Vec3d(1, 0, 0)), //
  SOUTH("south", new Vec3d(0, 0, 1)), //
  WEST("west", new Vec3d(-1, 0, 0)), //
  FORWARD("forward") {
    @Override
    public Vec3d getDirectionVec(float rotationYaw) {
      return MathUtil.getVectorForRotation(rotationYaw);
    }
  }, //
  BACK("back") {
    @Override
    public Vec3d getDirectionVec(float rotationYaw) {
      return MathUtil.getVectorForRotation(rotationYaw + 180);
    }
  }, //
  LEFT("left") {
    @Override
    public Vec3d getDirectionVec(float rotationYaw) {
      return MathUtil.getVectorForRotation(rotationYaw - 90);
    }
  }, //
  RIGHT("right") {
    @Override
    public Vec3d getDirectionVec(float rotationYaw) {
      return MathUtil.getVectorForRotation(rotationYaw + 90);
    }
  };

  private static final Map<String, Direction> BY_NAME = Maps.<String, Direction>newHashMap();
  static {
    for (Direction value : Direction.values()) {
      BY_NAME.put(value.getName(), value);
    }
  }

  public static @Nullable Direction byName(@Nullable String name) {
    return name == null ? null : (Direction) BY_NAME.get(name.toLowerCase(Locale.ROOT));
  }

  private final String name;
  private final @Nullable Vec3d directionVec;

  private Direction(String name) {
    this(name, null);
  }

  private Direction(String name, Vec3d directionVec) {
    this.name = name;
    this.directionVec = directionVec;
  }

  public String getName() {
    return name;
  }

  public Vec3d getDirectionVec(float rotationYaw) {
    return directionVec;
  }
}
