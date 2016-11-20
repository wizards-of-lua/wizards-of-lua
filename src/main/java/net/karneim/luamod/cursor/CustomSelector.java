package net.karneim.luamod.cursor;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CustomSelector {
  private final Cursor cursor;
  private final SelectionStrategy strategy;

  public CustomSelector(Cursor cursor, SelectionStrategy strategy) {
    this.cursor = cursor;
    this.strategy = strategy;
  }

  public Selection getSelection() {
    Selection selection = new Selection();
    World world = cursor.getWorld();
    BlockPos worldPosition = cursor.getWorldPosition();
    Rotation rotation = cursor.getRotation();
    selection.addAll(strategy.getPositions(world, worldPosition, rotation));
    return selection;
  }

}
