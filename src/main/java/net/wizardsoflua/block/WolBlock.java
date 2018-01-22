package net.wizardsoflua.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class WolBlock {
  public abstract IBlockState getBlockState();
  public abstract NBTTagCompound getNbt();
  public abstract void setBlock(World world, BlockPos pos);
  public abstract ItemStack asItemStack(int amount);
}
