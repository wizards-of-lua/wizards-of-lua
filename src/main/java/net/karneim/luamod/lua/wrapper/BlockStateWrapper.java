package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.DynamicTable;
import net.minecraft.block.state.IBlockState;

public class BlockStateWrapper extends StructuredLuaWrapper<IBlockState> {
  public BlockStateWrapper(@Nullable IBlockState delegate) {
    super(delegate);
  }

  @Override
  protected void addProperties(DynamicTable.Builder builder) {
    super.addProperties(builder);
    builder.add("type", "Block");
    builder.add("name", delegate.getBlock().getRegistryName().getResourcePath());
    // EnumFacing facing = delegate.getValue(BlockHorizontal.FACING);
    // if ( facing != null) {
    // builder.add("facing", new EnumWrapper(facing).getLuaObject());
    // }
  }

}
