package net.karneim.luamod;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Blocks {

  private final World world;

  public Blocks(World world) {
    this.world = world;
  }

  public @Nullable NBTTagCompound getData(BlockPos pos) {
    TileEntity tileentity = world.getTileEntity(pos);
    if (tileentity == null) {
      return null;
    }
    NBTTagCompound tagCompound = tileentity.writeToNBT(new NBTTagCompound());
    return tagCompound;
  }

  public void putData(BlockPos pos, NBTTagCompound data) {
    IBlockState iblockstate = world.getBlockState(pos);
    TileEntity tileentity = world.getTileEntity(pos);
    if (tileentity == null) {
      throw new IllegalArgumentException("Block at pos " + pos + " has no block data!");
    }
    data.setInteger("x", pos.getX());
    data.setInteger("y", pos.getY());
    data.setInteger("z", pos.getZ());
    tileentity.markDirty();
    world.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
    tileentity.readFromNBT(data);
  }

}
