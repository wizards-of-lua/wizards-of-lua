package net.wizardsoflua.lua.wrapper.block;

import net.minecraft.block.state.IBlockState;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.table.DefaultTableBuilder;
import net.wizardsoflua.lua.wrapper.WrapperFactory;

public class BlockWrapper {

  public static final String METATABLE_NAME = "Block";

  private final WrapperFactory wrappers;
  private final Table luaTable;

  public BlockWrapper(WrapperFactory wrappers, IBlockState delegate) {
    this.wrappers = wrappers;
    DefaultTableBuilder builder = new DefaultTableBuilder();
    builder.add("name", delegate.getBlock().getRegistryName().getResourcePath());

    builder.setMetatable((Table) wrappers.getEnv().rawget(METATABLE_NAME));

    luaTable = builder.build();
  }

  public Table getLuaTable() {
    return luaTable;
  }
}
