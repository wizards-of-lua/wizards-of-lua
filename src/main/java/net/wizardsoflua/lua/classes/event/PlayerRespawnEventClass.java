package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = PlayerRespawnEventClass.NAME, superClass = EventClass.class)
public class PlayerRespawnEventClass extends
    ProxyingLuaClass<PlayerEvent.PlayerRespawnEvent, PlayerRespawnEventClass.Proxy<PlayerEvent.PlayerRespawnEvent>> {
  public static final String NAME = "PlayerRespawnEvent";

  @Override
  public Proxy<PlayerEvent.PlayerRespawnEvent> toLua(PlayerEvent.PlayerRespawnEvent javaObj) {
    return new Proxy<>(getConverters(), getMetaTable(), javaObj);
  }

  public static class Proxy<D extends PlayerEvent.PlayerRespawnEvent> extends EventClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addImmutable("player", getConverters().toLua(delegate.player));
      addImmutable("endConquered", getConverters().toLua(delegate.isEndConquered()));
    }
  }
}
