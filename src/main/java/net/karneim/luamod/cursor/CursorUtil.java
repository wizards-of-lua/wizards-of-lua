package net.karneim.luamod.cursor;

import static net.minecraftforge.common.ForgeHooks.rayTraceEyes;

import javax.annotation.Nullable;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class CursorUtil {
  private static final double LOOK_DISTANCE = 5;

  @Deprecated
  public static Cursor createCursor(ICommandSender sender) {
    BlockPos pos = CursorUtil.getPositionLookingAt(sender.getCommandSenderEntity());
    EnumFacing facing = sender.getCommandSenderEntity().getHorizontalFacing();
    Rotation rotation = CursorUtil.getRotation(facing);
    EnumFacing side = CursorUtil.getSideLookingAt(sender.getCommandSenderEntity());
    EnumFacing surface = side == null ? null : side;
    Cursor cursor = new Cursor(sender, sender, sender.getEntityWorld(), pos, rotation, surface);
    return cursor;
  }

  public static BlockPos getPositionLookingAt(Entity entity) {
    RayTraceResult git = rayTraceEyes((EntityLivingBase) entity, LOOK_DISTANCE);
    return git == null ? getPositionAtLookDistance(entity) : git.getBlockPos();
  }

  public static @Nullable EnumFacing getSideLookingAt(Entity entity) {
    RayTraceResult git = rayTraceEyes((EntityLivingBase) entity, LOOK_DISTANCE);
    return git == null ? null : git.sideHit;
  }

  public static BlockPos getPositionAtLookDistance(Entity entity) {
    Vec3d startPos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
    Vec3d endPos = startPos.add(new Vec3d(entity.getLookVec().xCoord * LOOK_DISTANCE,
        entity.getLookVec().yCoord * LOOK_DISTANCE, entity.getLookVec().zCoord * LOOK_DISTANCE));
    return new BlockPos(endPos);
  }

  public static Rotation getRotation(EnumFacing facing) {
    switch (facing) {
      case NORTH:
        return Rotation.NONE;
      case EAST:
        return Rotation.CLOCKWISE_90;
      case SOUTH:
        return Rotation.CLOCKWISE_180;
      case WEST:
        return Rotation.COUNTERCLOCKWISE_90;
      default:
        return null;
    }
  }

  public static EnumFacing getFacing(Rotation rotation) {
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
        return null;
    }
  }
}
