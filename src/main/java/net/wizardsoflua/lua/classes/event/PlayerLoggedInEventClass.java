package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;

@DeclareLuaClass(name = PlayerLoggedInEventClass.NAME, superClass = EventClass.class)
public class PlayerLoggedInEventClass extends
    DelegatorLuaClass<PlayerEvent.PlayerLoggedInEvent, PlayerLoggedInEventClass.Proxy<PlayerEvent.PlayerLoggedInEvent>> {
  public static final String NAME = "PlayerLoggedInEvent";

  @Override
  public Proxy<PlayerEvent.PlayerLoggedInEvent> toLua(PlayerEvent.PlayerLoggedInEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends PlayerEvent.PlayerLoggedInEvent>
      extends EventClass.Proxy<EventApi<D>, D> {
    public Proxy(DelegatorLuaClass<?, ?> luaClass, D delegate) {
      super(new EventApi<>(luaClass, delegate));
      addImmutable("player", getConverters().toLua(delegate.player));
    }
  }
}
