package net.wizardsoflua.lua.classes.block;

import net.minecraft.block.material.Material;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyCachingLuaClass;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;

@DeclareLuaClass(name = MaterialClass.METATABLE_NAME)
public class MaterialClass
    extends ProxyCachingLuaClass<Material, MaterialClass.Proxy<Material>> {
  public static final String METATABLE_NAME = "Material";

  @Override
  protected String getMetatableName() {
    return METATABLE_NAME;
  }

  @Override
  public Proxy<Material> toLua(Material delegate) {
    return new Proxy<>(getConverters(), getMetatable(), delegate);
  }

  public static class Proxy<D extends Material> extends DelegatingProxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addImmutable("blocksLight", delegate.blocksLight());
      addImmutable("blocksMovement", delegate.blocksMovement());
      addImmutable("canBurn", delegate.getCanBurn());
      addImmutable("liquid", delegate.isLiquid());
      addImmutable("mobility", getMobilityFlag());
      addImmutable("opaque", delegate.isOpaque());
      addImmutable("replaceable", delegate.isReplaceable());
      addImmutable("requiresNoTool", delegate.isToolNotRequired());
      addImmutable("solid", delegate.isSolid());
    }

    @Override
    public boolean isTransferable() {
      return true;
    }

    private Object getMobilityFlag() {
      return getConverters().toLua(delegate.getMobilityFlag());
    }
  }
}
