package net.wizardsoflua.lua.classes.block;

import net.minecraft.block.material.Material;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;

public class MaterialClass {
  public static final String METATABLE_NAME = "Material";

  private final Converters converters;
  private final Table metatable;

  public MaterialClass(Converters converters) {
    this.converters = converters;
    // TODO do declaration outside this class
    this.metatable = converters.getTypes().declare(METATABLE_NAME);
  }

  public Table toLua(Material delegate) {
    return new Proxy(converters, metatable, delegate);
  }

  public static class Proxy extends DelegatingProxy {

    public Proxy(Converters converters, Table metatable, Material delegate) {
      super(converters, metatable, delegate);
      addImmutable("blocksLight", delegate.blocksLight());
      addImmutable("blocksMovement", delegate.blocksMovement());
      addImmutable("canBurn", delegate.getCanBurn());
      addImmutable("liquid", delegate.isLiquid());
      addImmutable("mobility", converters.enumToLua(delegate.getMobilityFlag()));
      addImmutable("opaque", delegate.isOpaque());
      addImmutable("replaceable", delegate.isReplaceable());
      addImmutable("requiresNoTool", delegate.isToolNotRequired());
      addImmutable("solid", delegate.isSolid());
    }
  }

}
