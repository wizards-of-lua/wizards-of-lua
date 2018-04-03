package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = PlayerRespawnEventClass.NAME, superClass = EventClass.class)
public class PlayerRespawnEventClass extends
    ProxyingLuaClass<PlayerEvent.PlayerRespawnEvent, PlayerRespawnEventClass.Proxy<PlayerEvent.PlayerRespawnEvent>> {
  public static final String NAME = "PlayerRespawnEvent";

  @Override
  public Proxy<PlayerEvent.PlayerRespawnEvent> toLua(PlayerEvent.PlayerRespawnEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends PlayerEvent.PlayerRespawnEvent>
      extends EventClass.Proxy<EventApi<D>, D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(new EventApi<>(luaClass, delegate));
      addImmutable("player", getConverters().toLua(delegate.player));
      addImmutable("endConquered", getConverters().toLua(delegate.isEndConquered()));
    }
  }
}
