package net.karneim.luamod.cursor;

import java.util.Iterator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockPosUtil {
  public static BlockPos getMinimum(BlockPos pos1, BlockPos pos2) {
    int minX = Math.min(pos1.getX(), pos2.getX());
    int minY = Math.min(pos1.getY(), pos2.getY());
    int minZ = Math.min(pos1.getZ(), pos2.getZ());
    return new BlockPos(minX, minY, minZ);
  }

  public static BlockPos getMaximum(BlockPos vec1, BlockPos vec2) {
    int maxX = Math.max(vec1.getX(), vec2.getX());
    int maxY = Math.max(vec1.getY(), vec2.getY());
    int maxZ = Math.max(vec1.getZ(), vec2.getZ());
    return new BlockPos(maxX, maxY, maxZ);
  }

  public static boolean isBetween(BlockPos pos, BlockPos min, BlockPos max) {
    return isGreaterOrEqual(pos, min) && isLessOrEqual(pos, max);
  }

  public static boolean isLessOrEqual(BlockPos pos, BlockPos other) {
    return isGreaterOrEqual(other, pos);
  }

  public static boolean isGreaterOrEqual(BlockPos pos, BlockPos other) {
    return pos.getX() >= other.getX() && pos.getY() >= other.getY() && pos.getZ() >= other.getZ();
  }

  public static Iterator<MutableBlockPos> toMutableBlockPosIterator(
      Iterable<BlockPos> blockPosCollection) {
    final Iterator<BlockPos> it = blockPosCollection.iterator();
    final MutableBlockPos pos = new MutableBlockPos();
    Iterator<MutableBlockPos> result = new Iterator<BlockPos.MutableBlockPos>() {

      @Override
      public boolean hasNext() {
        return it.hasNext();
      }

      @Override
      public MutableBlockPos next() {
        pos.setPos(it.next());
        return pos;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("remove");
      }

    };
    return result;
  }

  public static BlockPos getCenter(BlockPos pos1, BlockPos pos2) {
    int minx = pos1.getX();
    int maxx = pos2.getX();
    int miny = pos1.getY();
    int maxy = pos2.getY();
    int minz = pos1.getZ();
    int maxz = pos2.getZ();
    return new BlockPos(minx + (maxx - minx + 1) / 2, miny + (maxy - miny + 1) / 2,
        minz + (maxz - minz + 1) / 2);
  }

  public static BlockPos rotate(BlockPos pos, Rotation rotation) {
    switch (rotation) {
      case NONE:
        return new BlockPos(pos.getX(), pos.getY(), pos.getZ());
      case CLOCKWISE_90:
        return new BlockPos(-pos.getZ(), pos.getY(), pos.getX());
      case CLOCKWISE_180:
        return new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
      case COUNTERCLOCKWISE_90:
        return new BlockPos(pos.getZ(), pos.getY(), -pos.getX());
      default:
        throw new Error("WTF?");
    }
  }

  public static Vec3d rotate(Vec3d pos, Rotation rotation) {
    switch (rotation) {
      case NONE:
        return new Vec3d(pos.xCoord, pos.yCoord, pos.zCoord);
      case CLOCKWISE_90:
        return new Vec3d(-pos.zCoord, pos.yCoord, pos.xCoord);
      case CLOCKWISE_180:
        return new Vec3d(-pos.xCoord, pos.yCoord, -pos.zCoord);
      case COUNTERCLOCKWISE_90:
        return new Vec3d(pos.zCoord, pos.yCoord, -pos.xCoord);
      default:
        throw new Error("WTF?");
    }
  }

  public static IBlockState rotate(IBlockState blkState, Rotation rotation) {
    return blkState.withRotation(rotation);
  }
}
