package net.karneim.luamod.lua.wrapper;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.karneim.luamod.lua.LuaTypeConverter;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.sandius.rembulan.Table;

public class BlockStateWrapper extends DelegatingTableWrapper<IBlockState> {
  public BlockStateWrapper(Table env, @Nullable IBlockState delegate) {
    super(env, delegate);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    builder.add("type", "Block");
    builder.addNullable("name", delegate.getBlock().getRegistryName().getResourcePath());
    Map<String, Object> props = Maps.newHashMap();
    Collection<IProperty<?>> names = delegate.getPropertyNames();
    for (IProperty<?> name : names) {
      Object value = delegate.getValue(name);
      Object luaValue = LuaTypeConverter.luaValueOf(value);
      props.put(name.getName(), luaValue);
    }
    builder.addNullable("properties", new StringXLuaObjectMapWrapper(env, props).getLuaObject());
    builder.addNullable("material", new MaterialWrapper(env, delegate.getMaterial()).getLuaObject());
  }

}
