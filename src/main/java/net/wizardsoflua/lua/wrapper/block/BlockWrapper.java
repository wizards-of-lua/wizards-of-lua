package net.wizardsoflua.lua.wrapper.block;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.table.DefaultTableBuilder;
import net.wizardsoflua.lua.table.PatchedImmutableTable;
import net.wizardsoflua.lua.wrapper.Wrappers;

public class BlockWrapper {
  public static final String METATABLE_NAME = "Block";

  private final Wrappers wrappers;
  private final Table metatable;

  public BlockWrapper(Wrappers wrappers) {
    this.wrappers = wrappers;
    // TODO do declaration outside this class
    this.metatable = wrappers.getTypes().declare(METATABLE_NAME);
  }

  public Table wrap(IBlockState delegate) {
    DefaultTableBuilder builder = new DefaultTableBuilder();
    builder.setMetatable(metatable);
    builder.add("name", delegate.getBlock().getRegistryName().getResourcePath());
    builder.add("material", wrappers.wrap(delegate.getMaterial()));
    builder.add("properties", getProperties(delegate));


    return builder.build();
  }

  public Table getProperties(IBlockState delegate) {
    Map<Object, Object> map = new HashMap<>();
    Collection<IProperty<?>> names = delegate.getPropertyKeys();
    for (IProperty<?> name : names) {
      Object value = delegate.getValue(name);
      Object luaValue = McPropertyValueToLuaTypeConverter.luaValueOf(value);
      map.put(name.getName(), luaValue);
    }
    return PatchedImmutableTable.of(map);
  }

}
