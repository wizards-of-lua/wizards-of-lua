package net.karneim.luamod.cursor;

import net.minecraft.util.math.BlockPos;

public class BoxSelector {
  private final Cursor cursor;

  private BlockPos fromPos;
  private BlockPos toPos;

  public BoxSelector(Cursor cursor) {
    this.cursor = cursor;
  }

  public void startSelection() {
    fromPos = cursor.getWorldPosition();
  }

  public void finishSelection() {
    toPos = cursor.getWorldPosition();
  }

  public Selection getSelection() {
    Selection result = new Selection();
    BlockPos min = BlockPosUtil.getMinimum(fromPos, toPos);
    BlockPos max = BlockPosUtil.getMaximum(fromPos, toPos);
    for (int y = min.getY(); y < max.getY(); ++y) {
      for (int z = min.getZ(); z < max.getZ(); ++z) {
        for (int x = min.getX(); x < max.getX(); ++x) {
          result.add(new BlockPos(x, y, z));
        }
      }
    }
    return result;
  }

}
