package net.wizardsoflua.lua.wrapper.block;

import net.minecraft.block.material.Material;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.table.DefaultTableBuilder;
import net.wizardsoflua.lua.wrapper.Wrappers;

public class MaterialWrapper {
  public static final String METATABLE_NAME = "Material";

  private final Wrappers wrappers;
  private final Table metatable;


  public MaterialWrapper(Wrappers wrappers) {
    this.wrappers = wrappers;
    // TODO do declaration outside this class
    this.metatable = wrappers.getTypes().declare(METATABLE_NAME);
  }

  public Table wrap(Material delegate) {
    DefaultTableBuilder b = new DefaultTableBuilder();

    b.add("blocksLight", delegate.blocksLight());
    b.add("blocksMovement", delegate.blocksMovement());
    b.add("canBurn", delegate.getCanBurn());
    b.add("isLiquid", delegate.isLiquid());
    b.add("isOpaque", delegate.isOpaque());
    b.add("isSolid", delegate.isSolid());
    b.add("isToolNotRequired", delegate.isToolNotRequired());
    b.add("mobility", wrappers.wrap(delegate.getMobilityFlag()));

    b.setMetatable(metatable);

    return b.build();
  }

}
