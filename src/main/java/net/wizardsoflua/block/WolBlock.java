package net.wizardsoflua.block;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class WolBlock {
  private IBlockState blockState;
  private final @Nullable NBTTagCompound data;

  public WolBlock(IBlockState blockState, NBTTagCompound data) {
    this.blockState = blockState;
    this.data = data;
  }

  public WolBlock(IBlockState blockState, @Nullable TileEntity tileEntity) {
    this.blockState = blockState;
    if (tileEntity != null) {
      NBTTagCompound tagCompound = new NBTTagCompound();
      tileEntity.writeToNBT(tagCompound);
      data = tagCompound;
    } else {
      data = null;
    }
  }

  public IBlockState getBlockState() {
    return blockState;
  }

  public void setBlockState(IBlockState blockState) {
    this.blockState = blockState;
  }

  public NBTTagCompound getData() {
    return data;
  }
}
