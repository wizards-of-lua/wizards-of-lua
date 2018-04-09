package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;

@DeclareLuaClass(name = LeftClickBlockEventClass.NAME, superClass = PlayerInteractEventClass.class)
public class LeftClickBlockEventClass extends
    DelegatorLuaClass<PlayerInteractEvent.LeftClickBlock, LeftClickBlockEventClass.Proxy<PlayerInteractEvent.LeftClickBlock>> {
  public static final String NAME = "LeftClickBlockEvent";

  @Override
  public Proxy<PlayerInteractEvent.LeftClickBlock> toLua(
      PlayerInteractEvent.LeftClickBlock javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends PlayerInteractEvent.LeftClickBlock>
      extends PlayerInteractEventClass.Proxy<D> {
    public Proxy(DelegatorLuaClass<?, ?> luaClass, D delegate) {
      super(luaClass, delegate);
      addImmutableNullable("hitVec", getConverter().toLuaNullable(delegate.getHitVec()));
    }
  }
}
