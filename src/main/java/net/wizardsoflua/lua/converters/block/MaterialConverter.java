package net.wizardsoflua.lua.converters.block;

import net.minecraft.block.material.Material;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.converters.Converters;
import net.wizardsoflua.lua.table.DefaultTableBuilder;

public class MaterialConverter {
  public static final String METATABLE_NAME = "Material";

  private final Converters converters;
  private final Table metatable;


  public MaterialConverter(Converters converters) {
    this.converters = converters;
    // TODO do declaration outside this class
    this.metatable = converters.getTypes().declare(METATABLE_NAME);
  }

  public Table toLua(Material delegate) {
    DefaultTableBuilder b = new DefaultTableBuilder();
    b.setMetatable(metatable);

    b.add("blocksLight", delegate.blocksLight());
    b.add("blocksMovement", delegate.blocksMovement());
    b.add("canBurn", delegate.getCanBurn());
    b.add("isLiquid", delegate.isLiquid());
    b.add("isOpaque", delegate.isOpaque());
    b.add("isSolid", delegate.isSolid());
    b.add("isToolNotRequired", delegate.isToolNotRequired());
    b.add("mobility", converters.enumToLua(delegate.getMobilityFlag()));


    return b.build();
  }

}
