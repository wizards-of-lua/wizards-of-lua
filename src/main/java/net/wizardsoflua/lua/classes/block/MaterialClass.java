package net.wizardsoflua.lua.classes.block;

import net.minecraft.block.material.Material;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.InstanceCachingLuaClass;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;

@DeclareLuaClass(name = MaterialClass.METATABLE_NAME)
public class MaterialClass extends InstanceCachingLuaClass<Material> {
  public static final String METATABLE_NAME = "Material";

  public MaterialClass() {
    super(Material.class);
  }

  @Override
  public Proxy toLua(Material delegate) {
    return new Proxy(getConverters(), getMetatable(), delegate);
  }

  @Override
  public Material toJava(Table luaObj) {
    Proxy proxy = getProxy(luaObj);
    return proxy.delegate;
  }

  protected Proxy getProxy(Object luaObj) {
    getConverters().getTypes().checkAssignable(METATABLE_NAME, luaObj);
    return (Proxy) luaObj;
  }

  public static class Proxy extends DelegatingProxy {

    private Material delegate;

    public Proxy(Converters converters, Table metatable, Material delegate) {
      super(converters, metatable, delegate);
      this.delegate = delegate;
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
