package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = PlayerLoggedInEventClass.NAME, superClass = EventClass.class)
public class PlayerLoggedInEventClass extends
    ProxyingLuaClass<PlayerEvent.PlayerLoggedInEvent, PlayerLoggedInEventClass.Proxy<PlayerEvent.PlayerLoggedInEvent>> {
  public static final String NAME = "PlayerLoggedInEvent";

  @Override
  public Proxy<PlayerEvent.PlayerLoggedInEvent> toLua(PlayerEvent.PlayerLoggedInEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends PlayerEvent.PlayerLoggedInEvent>
      extends EventClass.Proxy<EventApi<D>, D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(new EventApi<>(luaClass, delegate));
      addImmutable("player", getConverter().toLua(delegate.player));
    }
  }
}
