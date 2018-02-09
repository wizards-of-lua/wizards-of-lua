package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = LeftClickBlockEventClass.NAME, superClass = PlayerInteractEventClass.class)
public class LeftClickBlockEventClass extends
    ProxyingLuaClass<PlayerInteractEvent.LeftClickBlock, LeftClickBlockEventClass.Proxy<PlayerInteractEvent.LeftClickBlock>> {
  public static final String NAME = "LeftClickBlockEvent";

  @Override
  public Proxy<PlayerInteractEvent.LeftClickBlock> toLua(
      PlayerInteractEvent.LeftClickBlock javaObj) {
    return new Proxy<>(getConverters(), getMetaTable(), javaObj);
  }

  public static class Proxy<D extends PlayerInteractEvent.LeftClickBlock>
      extends PlayerInteractEventClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addImmutableNullable("hitVec", getConverters().toLuaNullable(delegate.getHitVec()));
    }
  }
}
