package net.wizardsoflua.block;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LiveWolBlock extends WolBlock {

  private BlockPos pos;
  private World world;

  public LiveWolBlock(BlockPos pos, World world) {
    this.pos = checkNotNull(pos, "pos==null!");
    this.world = checkNotNull(world, "world==null!");
  }

  @Override
  public IBlockState getBlockState() {
    return world.getBlockState(pos);
  }

  @Override
  public NBTTagCompound getNbt() {
    TileEntity tileEntity = world.getTileEntity(pos);
    if (tileEntity == null) {
      return null;
    }
    NBTTagCompound result = new NBTTagCompound();
    tileEntity.write(result);
    return result;
  }

  @Override
  public void setBlock(World targetWorld, BlockPos targetPos) {
    if (world == targetWorld && pos.equals(targetPos)) {
      return;
    }
    TileEntity tileEntity = targetWorld.getTileEntity(targetPos);
    if (tileEntity != null) {
      if (tileEntity instanceof IInventory) {
        ((IInventory) tileEntity).clear();
      }
    }
    IBlockState blockState = getBlockState();
    targetWorld.setBlockState(targetPos, blockState, 2);
    NBTTagCompound nbt = getNbt();
    if (nbt != null) {
      // TODO remove this side effect
      // however, it does not hurt
      nbt.setInt("x", targetPos.getX());
      nbt.setInt("y", targetPos.getY());
      nbt.setInt("z", targetPos.getZ());

      tileEntity = targetWorld.getTileEntity(targetPos);
      if (tileEntity != null) {
        tileEntity.read(nbt);
        tileEntity.markDirty();
        nbt.removeTag("x");
        nbt.removeTag("y");
        nbt.removeTag("z");
      } else {
        throw new IllegalStateException(String.format("Missing tile entity for %s at %s %s %s",
            blockState.getBlock().getRegistryName(), targetPos.getX(), targetPos.getY(),
            targetPos.getZ()));
      }
      int flags = 3; // Do a block update (1) and send it to all clients (2)
      targetWorld.notifyBlockUpdate(targetPos, blockState, blockState, flags);
    }
  }

  @Override
  public ItemStack asItemStack(int amount) {
    return ItemUtil.getItemStackFromBlock(getBlockState(), getNbt(), amount);
  }

}
