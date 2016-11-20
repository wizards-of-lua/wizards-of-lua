package net.karneim.luamod.cursor;

import static net.karneim.luamod.cursor.BlockPosUtil.getMaximum;
import static net.karneim.luamod.cursor.BlockPosUtil.getMinimum;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class ContinuousSelectionStrategy implements SelectionStrategy {

  private final BlockMask includeBlockMask;
  private final int radiusXZ;
  private final int radiusDown;
  private final int radiusUp;

  public ContinuousSelectionStrategy(BlockMask includeBlockMask, int radius) {
    this(includeBlockMask, radius, 0, 255);
  }

  public ContinuousSelectionStrategy(BlockMask includeBlockMask, int radiusXZ,
      int radiusDown, int radiusUp) {
    this.includeBlockMask = includeBlockMask;
    this.radiusXZ = radiusXZ;
    this.radiusDown = radiusDown;
    this.radiusUp = radiusUp;
  }

  @Override
  public Collection<BlockPos> getPositions(World world, BlockPos start, Rotation rotation) {
    BlockPos pos1 = new BlockPos(start.getX() - radiusXZ, Math.max(0, start.getY() - radiusDown),
        start.getZ() - radiusXZ);
    BlockPos pos2 = new BlockPos(start.getX() + radiusXZ, Math.min(255, start.getY() + radiusUp),
        start.getZ() + radiusXZ);
    StructureBoundingBox boundingBox = new StructureBoundingBox(pos1, pos2);

    BlockPos minimumPoint = start;
    BlockPos maximumPoint = start;
    Neighbours neighbours = new Neighbours();
    LinkedList<BlockPos> todo = Lists.newLinkedList();
    Set<BlockPos> done = Sets.newHashSet();
    Set<BlockPos> result = Sets.newHashSet();
    todo.push(start);

    while (!todo.isEmpty()) {
      BlockPos v = todo.pop();
      if (includeBlockMask.accepts(v, world.getBlockState(v)) && !done.contains(v)) {
        result.add(v);
        maximumPoint = getMaximum(v, maximumPoint);
        minimumPoint = getMinimum(v, minimumPoint);
        neighbours.fill(v);
        for (BlockPos pos : neighbours.positions) {
          if (boundingBox.isVecInside(pos) && !done.contains(pos)) {
            todo.add(pos);
          }
        }
      }
      done.add(v);
    }
    return result;
  }

  private static class Neighbours {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int WEST = 2;
    public static final int EAST = 3;
    public static final int UP = 4;
    public static final int DOWN = 5;
    private BlockPos[] positions = new BlockPos[6];

    public void fill(BlockPos v) {
      positions[NORTH] = v.add(0, 0, 1);
      positions[SOUTH] = v.add(0, 0, -1);
      positions[WEST] = v.add(-1, 0, 0);
      positions[EAST] = v.add(1, 0, 0);
      positions[UP] = v.add(0, 1, 0);
      positions[DOWN] = v.add(0, -1, 0);
    }
  }

}
