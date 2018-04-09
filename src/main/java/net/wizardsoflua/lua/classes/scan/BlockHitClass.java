package net.wizardsoflua.lua.classes.scan;

import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;
import net.wizardsoflua.lua.classes.common.LuaInstance;

@DeclareLuaClass(name = BlockHitClass.NAME)
public class BlockHitClass
    extends DelegatorLuaClass<RayTraceResult, BlockHitClass.Proxy<RayTraceResult>> {
  public static final String NAME = "BlockHit";

  @Override
  public Proxy<RayTraceResult> toLua(RayTraceResult javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends RayTraceResult> extends LuaInstance<D> {
    public Proxy(DelegatorLuaClass<?, ?> luaClass, D delegate) {
      super(luaClass, delegate);
      addImmutable("hitVec", getConverter().toLuaNullable(delegate.hitVec));
      addImmutable("pos", getConverter().toLuaNullable(new Vec3d(delegate.getBlockPos())));
      addImmutable("sideHit", getConverter().toLuaNullable(delegate.sideHit));
    }

    @Override
    public boolean isTransferable() {
      return true;
    }
  }
}
