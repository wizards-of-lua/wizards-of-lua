package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = PlayerItemPickupEventClass.NAME, superClass = EventClass.class)
public class PlayerItemPickupEventClass extends
    ProxyingLuaClass<PlayerEvent.ItemPickupEvent, PlayerItemPickupEventClass.Proxy<PlayerEvent.ItemPickupEvent>> {
  public static final String NAME = "PlayerItemPickupEvent";

  @Override
  public Proxy<PlayerEvent.ItemPickupEvent> toLua(PlayerEvent.ItemPickupEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends PlayerEvent.ItemPickupEvent>
      extends EventClass.Proxy<EventApi<D>, D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(new EventApi<>(luaClass, delegate));
      addImmutable("player", getConverter().toLua(delegate.player));
      addImmutable("item", getConverter().toLua(delegate.pickedUp));
    }
  }
}
