package net.wizardsoflua.block;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ImmutableWolBlock extends WolBlock {
  private final IBlockState blockState;
  private final @Nullable NBTTagCompound nbt;

  public ImmutableWolBlock(IBlockState blockState, @Nullable NBTTagCompound nbt) {
    this.blockState = checkNotNull(blockState, "blockState==null!");
    if (nbt != null) {
      this.nbt = nbt.copy();
      this.nbt.removeTag("x");
      this.nbt.removeTag("y");
      this.nbt.removeTag("z");
    } else {
      this.nbt = null;
    }
  }

  public ImmutableWolBlock(IBlockState blockState, @Nullable TileEntity tileEntity) {
    this.blockState = checkNotNull(blockState, "blockState==null!");
    if (tileEntity != null) {
      nbt = new NBTTagCompound();
      tileEntity.writeToNBT(nbt);
      nbt.removeTag("x");
      nbt.removeTag("y");
      nbt.removeTag("z");
    } else {
      nbt = null;
    }
  }

  @Override
  public IBlockState getBlockState() {
    return blockState;
  }

  @Override
  public NBTTagCompound getNbt() {
    return nbt;
  }

  @Override
  public void setBlock(World world, BlockPos pos) {
    TileEntity oldTileEntity = world.getTileEntity(pos);
    if (oldTileEntity != null) {
      if (oldTileEntity instanceof IInventory) {
        ((IInventory) oldTileEntity).clear();
      }
    }
    world.setBlockState(pos, blockState, 2);
    if (nbt != null) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity != null) {
        NBTTagCompound newNbt = nbt.copy();
        newNbt.setInteger("x", pos.getX());
        newNbt.setInteger("y", pos.getY());
        newNbt.setInteger("z", pos.getZ());
        tileEntity.readFromNBT(newNbt);
        tileEntity.markDirty();
      } else {
        throw new IllegalStateException(String.format("Missing tile entity for %s at %s %s %s",
            blockState.getBlock().getRegistryName(), pos.getX(), pos.getY(), pos.getZ()));
      }
      int flags = 1 + 2; // Do a block update (1) and send it to all clients (2)
      world.notifyBlockUpdate(pos, blockState, blockState, flags);
    }
  }

  @Override
  public ItemStack asItemStack(int amount) {
    return ItemUtil.getItemStackFromBlock(blockState, nbt, amount);
  }

}
