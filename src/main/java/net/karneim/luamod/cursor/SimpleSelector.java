package net.karneim.luamod.cursor;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SimpleSelector {
  private final Cursor cursor;
  private Selection selection = new Selection();

  public SimpleSelector(Cursor cursor) {
    this.cursor = cursor;
  }

  public Selection getSelection() {
    return selection;
  }

  public void select() {
    selection.add(cursor.getWorldPosition());
  }

  public void deselect() {
    selection.remove(cursor.getWorldPosition());
  }

  public void select(SelectionStrategy strategy) {
    World world = cursor.getWorld();
    BlockPos worldPosition = cursor.getWorldPosition();
    Rotation rotation = cursor.getRotation();
    selection.addAll(strategy.getPositions(world, worldPosition, rotation));
  }
}
