package net.wizardsoflua.lua.wrapper.block;

import net.minecraft.block.material.Material;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.table.DefaultTableBuilder;
import net.wizardsoflua.lua.wrapper.WrapperFactory;

public class MaterialWrapper {

  public static final String METATABLE_NAME = "Material";

  private final WrapperFactory wrappers;
  private final Table luaTable;

  // TODO no need for creating a class here. this can be done just by a function
  public MaterialWrapper(WrapperFactory wrappers, Material delegate) {
    this.wrappers = wrappers;
    DefaultTableBuilder b = new DefaultTableBuilder();
    
    b.add("blocksLight", delegate.blocksLight());
    b.add("blocksMovement", delegate.blocksMovement());
    b.add("canBurn", delegate.getCanBurn());
    b.add("isLiquid", delegate.isLiquid());
    b.add("isOpaque", delegate.isOpaque());
    b.add("isSolid", delegate.isSolid());
    b.add("isToolNotRequired", delegate.isToolNotRequired());
    b.add("mobility", wrappers.wrap(delegate.getMobilityFlag()));
    
    b.setMetatable((Table) wrappers.getEnv().rawget(METATABLE_NAME));

    luaTable = b.build();
  }

  public Table getLuaTable() {
    return luaTable;
  }
}
