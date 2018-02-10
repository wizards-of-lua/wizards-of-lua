package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = PlayerChangedDimensionEventClass.NAME, superClass = EventClass.class)
public class PlayerChangedDimensionEventClass extends
    ProxyingLuaClass<PlayerEvent.PlayerChangedDimensionEvent, PlayerChangedDimensionEventClass.Proxy<PlayerEvent.PlayerChangedDimensionEvent>> {
  public static final String NAME = "PlayerChangedDimensionEvent";

  @Override
  public Proxy<PlayerEvent.PlayerChangedDimensionEvent> toLua(
      PlayerEvent.PlayerChangedDimensionEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends PlayerEvent.PlayerChangedDimensionEvent>
      extends EventClass.Proxy<D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(luaClass, delegate);
      addImmutable("player", getConverters().toLua(delegate.player));
      addImmutable("from", getConverters().toLua(delegate.fromDim));
      addImmutable("to", getConverters().toLua(delegate.toDim));
    }
  }
}
