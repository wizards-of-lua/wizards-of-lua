package net.karneim.luamod.cursor;

import java.util.Collection;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface SelectionStrategy {

  Collection<BlockPos> getPositions(World world, BlockPos worldPosition, Rotation rotation);

}
