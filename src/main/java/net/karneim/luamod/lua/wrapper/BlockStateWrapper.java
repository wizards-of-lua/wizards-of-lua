package net.karneim.luamod.lua.wrapper;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.karneim.luamod.lua.DynamicTable;
import net.karneim.luamod.lua.LuaTypeConverter;
import net.minecraft.block.properties.IProperty;
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
    Map<String, Object> props = Maps.newHashMap();
    Collection<IProperty<?>> names = delegate.getPropertyNames();
    for (IProperty<?> name : names) {
      Object value = delegate.getValue(name);
      Object luaValue = LuaTypeConverter.luaValueOf(value);
      props.put(name.getName(), luaValue);
    }
    builder.add("properties", new StringXLuaObjectMapWrapper(props).getLuaObject());

    // EnumFacing facing = delegate.getValue(BlockHorizontal.FACING);
    // if ( facing != null) {
    // builder.add("facing", new EnumWrapper(facing).getLuaObject());
    // }
  }

}
