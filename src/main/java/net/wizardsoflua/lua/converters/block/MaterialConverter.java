package net.wizardsoflua.lua.converters.block;

import net.minecraft.block.material.Material;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.converters.Converters;
import net.wizardsoflua.lua.converters.common.DelegatingProxy;

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
    return new Proxy(converters, metatable, delegate);
  }

  public static class Proxy extends DelegatingProxy {

    public Proxy(Converters converters, Table metatable, Material delegate) {
      super(converters, metatable, delegate);
      addImmutable("blocksLight", delegate.blocksLight());
      addImmutable("blocksMovement", delegate.blocksMovement());
      addImmutable("canBurn", delegate.getCanBurn());
      addImmutable("isLiquid", delegate.isLiquid());
      addImmutable("isOpaque", delegate.isOpaque());
      addImmutable("isSolid", delegate.isSolid());
      addImmutable("isToolNotRequired", delegate.isToolNotRequired());
      addImmutable("mobility", converters.enumToLua(delegate.getMobilityFlag()));
    }
  }

}
