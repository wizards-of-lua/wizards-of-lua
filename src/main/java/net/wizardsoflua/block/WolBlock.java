package net.wizardsoflua.block;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

  public void setBlock(World world, BlockPos pos) {
    TileEntity oldTileEntity = world.getTileEntity(pos);
    if (oldTileEntity != null) {
      if (oldTileEntity instanceof IInventory) {
        ((IInventory) oldTileEntity).clear();
      }
    }
    world.setBlockState(pos, blockState, 2);
    if (nbt != null) {
      // TODO remove this side effect
      // however, it does not hurt
      nbt.setInteger("x", pos.getX());
      nbt.setInteger("y", pos.getY());
      nbt.setInteger("z", pos.getZ());

      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity != null) {
        tileEntity.readFromNBT(nbt);
        tileEntity.markDirty();
      } else {
        throw new IllegalStateException(String.format("Missing tile entity for %s at %s %s %s",
            blockState.getBlock().getRegistryName().getResourcePath(), pos.getX(), pos.getY(),
            pos.getZ()));
      }
      int flags = 3; // Do a block update (1) and send it to all clients (2)
      world.notifyBlockUpdate(pos, blockState, blockState, flags);
    }
  }
}
