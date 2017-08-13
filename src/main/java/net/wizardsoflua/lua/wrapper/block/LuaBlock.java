package net.wizardsoflua.lua.wrapper.block;

import net.minecraft.block.state.IBlockState;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.table.DefaultTableBuilder;
import net.wizardsoflua.lua.wrapper.WrapperFactory;

public class LuaBlock {
  public static final String METATABLE_NAME = "Block";

  private final WrapperFactory wrappers;
  private final Table metatable;


  public LuaBlock(WrapperFactory wrappers) {
    this.wrappers = wrappers;
    // TODO do declaration outside this class
    this.metatable = wrappers.getTypes().declare(METATABLE_NAME);
  }

  public Table wrap(IBlockState delegate) {
    DefaultTableBuilder builder = new DefaultTableBuilder();
    builder.add("name", delegate.getBlock().getRegistryName().getResourcePath());
    builder.add("material", wrappers.wrap(delegate.getMaterial()));

    builder.setMetatable(metatable);

    return builder.build();
  }

}
