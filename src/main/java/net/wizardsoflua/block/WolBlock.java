package net.wizardsoflua.block;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class WolBlock {
  private IBlockState blockState;
  private final @Nullable NBTTagCompound nbt;

  public WolBlock(IBlockState blockState, NBTTagCompound nbt) {
    this.blockState = blockState;
    this.nbt = nbt;
  }

  public WolBlock(IBlockState blockState, @Nullable TileEntity tileEntity) {
    this.blockState = blockState;
    if (tileEntity != null) {
      NBTTagCompound tagCompound = new NBTTagCompound();
      tileEntity.writeToNBT(tagCompound);
      nbt = tagCompound;
    } else {
      nbt = null;
    }
  }

  public IBlockState getBlockState() {
    return blockState;
  }

  public void setBlockState(IBlockState blockState) {
    this.blockState = blockState;
  }

  public NBTTagCompound getNbt() {
    return nbt;
  }
}
