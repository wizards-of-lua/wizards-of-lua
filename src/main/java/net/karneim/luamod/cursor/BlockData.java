package net.karneim.luamod.cursor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

class BlockData {
  public final BlockPos sourcePos;
  public final IBlockState sourceState;
  @Nullable
  public final NBTTagCompound tileEntityData;

  public BlockData(BlockPos sourcePos, IBlockState sourceState,
      @Nullable NBTTagCompound tileEntityData) {
    this.sourcePos = sourcePos;
    this.sourceState = sourceState;
    this.tileEntityData = tileEntityData;
  }
  
  public static List<BlockPos> selectBlockPos(Iterable<BlockData> itBlockData) {
    List<BlockPos> result = new ArrayList<BlockPos>();
    for( BlockData data:itBlockData) {
      result.add(data.sourcePos);
    }
    return result;
  }

}
