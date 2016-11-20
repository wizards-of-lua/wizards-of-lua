package net.karneim.luamod.cursor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class Selection implements Iterable<BlockPos> {

  private Set<BlockPos> positions = new HashSet<BlockPos>();

  public void addAll(Collection<BlockPos> newPositions) {
    positions.addAll(newPositions);
  }

  public void add(BlockPos pos) {
    positions.add(pos);
  }

  public void remove(BlockPos pos) {
    positions.remove(pos);
  }

  public boolean contains(BlockPos pos) {
    return positions.contains(pos);
  }

  public void clear() {
    positions.clear();
  }

  public boolean isEmpty() {
    return positions.isEmpty();
  }

  public Iterable<BlockPos> getPositions() {
    return positions;
  }

  @Override
  public Iterator<BlockPos> iterator() {
    return positions.iterator();
  }

  public StructureBoundingBox getBoundingBox() {
    BlockPos maxPos = null;
    BlockPos minPos = null;
    if (positions.isEmpty()) {
      return null;
    }
    for (BlockPos blockPos : positions) {
      if (maxPos == null) {
        maxPos = blockPos;
      }
      maxPos = BlockPosUtil.getMaximum(maxPos, blockPos);
      if (minPos == null) {
        minPos = blockPos;
      }
      minPos = BlockPosUtil.getMinimum(minPos, blockPos);
    }
    return new StructureBoundingBox(maxPos, minPos);
  }

}
