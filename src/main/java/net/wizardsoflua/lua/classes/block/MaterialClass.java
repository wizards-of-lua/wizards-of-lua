package net.wizardsoflua.lua.classes.block;

import net.minecraft.block.material.Material;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyCachingLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;
import net.wizardsoflua.lua.classes.common.LuaInstance;

@DeclareLuaClass(name = MaterialClass.NAME)
public class MaterialClass extends ProxyCachingLuaClass<Material, MaterialClass.Proxy<Material>> {
  public static final String NAME = "Material";

  @Override
  public Proxy<Material> toLua(Material delegate) {
    return new Proxy<>(this, delegate);
  }

  public static class Proxy<D extends Material> extends LuaInstance<D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(luaClass, delegate);
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
