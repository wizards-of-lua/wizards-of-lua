package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;

@DeclareLuaClass(name = RightClickBlockEventClass.NAME, superClass = PlayerInteractEventClass.class)
public class RightClickBlockEventClass extends
    DelegatorLuaClass<PlayerInteractEvent.RightClickBlock, RightClickBlockEventClass.Proxy<PlayerInteractEvent.RightClickBlock>> {
  public static final String NAME = "RightClickBlockEvent";

  @Override
  public Proxy<PlayerInteractEvent.RightClickBlock> toLua(
      PlayerInteractEvent.RightClickBlock javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends PlayerInteractEvent.RightClickBlock>
      extends PlayerInteractEventClass.Proxy<D> {
    public Proxy(DelegatorLuaClass<?, ?> luaClass, D delegate) {
      super(luaClass, delegate);
      addImmutableNullable("hitVec", getConverters().toLuaNullable(delegate.getHitVec()));
    }
  }
}
