package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = PlayerLoggedOutEventClass.NAME, superClass = EventClass.class)
public class PlayerLoggedOutEventClass extends
    ProxyingLuaClass<PlayerEvent.PlayerLoggedOutEvent, PlayerLoggedOutEventClass.Proxy<PlayerEvent.PlayerLoggedOutEvent>> {
  public static final String NAME = "PlayerLoggedOutEvent";

  @Override
  public Proxy<PlayerEvent.PlayerLoggedOutEvent> toLua(PlayerEvent.PlayerLoggedOutEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends PlayerEvent.PlayerLoggedOutEvent>
      extends EventClass.Proxy<D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(luaClass, delegate);
      addImmutable("player", getConverters().toLua(delegate.player));
    }
  }
}
