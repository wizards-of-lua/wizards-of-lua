package net.karneim.luamod.cursor;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;

public enum EnumDirection {
  FORWARD(0), BACK(180), LEFT(-90), RIGHT(90), UP(0), DOWN(0);


  private static final Map<String, EnumDirection> NAME_LOOKUP =
      Maps.<String, EnumDirection>newHashMap();

  static {
    for (EnumDirection value : values()) {
      NAME_LOOKUP.put(value.name(), value);
    }
  }

  /**
   * Get the direction specified by the given name
   */
  @Nullable
  public static EnumDirection byName(String name) {
    return name == null ? null : (EnumDirection) NAME_LOOKUP.get(name);
  }

  private final float horizontalAngle;

  private EnumDirection(float horizontalAngle) {
    this.horizontalAngle = horizontalAngle;
  }
  
  public float getHorizontalAngle() {
    return horizontalAngle;
  }
  
  /**
   * Returns the absolute facing of this direction modified by the given rotation.
   * 
   * @param rotation
   * @param up2
   * @return the absolute facing
   */
  public EnumFacing rotate(Rotation rotation) {
    switch (this) {
      case UP:
        return EnumFacing.UP;
      case DOWN:
        return EnumFacing.DOWN;
      case FORWARD:
        switch (rotation) {
          case NONE:
            return EnumFacing.NORTH;
          case CLOCKWISE_90:
            return EnumFacing.EAST;
          case CLOCKWISE_180:
            return EnumFacing.SOUTH;
          case COUNTERCLOCKWISE_90:
            return EnumFacing.WEST;
          default:
            throw new Error("WTF?");
        }
      case RIGHT:
        switch (rotation) {
          case NONE:
            return EnumFacing.EAST;
          case CLOCKWISE_90:
            return EnumFacing.SOUTH;
          case CLOCKWISE_180:
            return EnumFacing.WEST;
          case COUNTERCLOCKWISE_90:
            return EnumFacing.NORTH;
          default:
            throw new Error("WTF?");
        }
      case BACK:
        switch (rotation) {
          case NONE:
            return EnumFacing.SOUTH;
          case CLOCKWISE_90:
            return EnumFacing.WEST;
          case CLOCKWISE_180:
            return EnumFacing.NORTH;
          case COUNTERCLOCKWISE_90:
            return EnumFacing.EAST;
          default:
            throw new Error("WTF?");
        }
      case LEFT:
        switch (rotation) {
          case NONE:
            return EnumFacing.WEST;
          case CLOCKWISE_90:
            return EnumFacing.NORTH;
          case CLOCKWISE_180:
            return EnumFacing.EAST;
          case COUNTERCLOCKWISE_90:
            return EnumFacing.SOUTH;
          default:
            throw new Error("WTF?");
        }
      default:
        throw new Error("WTF?");
    }
  }

  public boolean isHorizontal() {
    switch (this) {
      case UP:
      case DOWN:
        return false;
      default:
        return true;
    }
  }
}
