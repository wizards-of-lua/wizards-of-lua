package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = PlayerItemPickupEventClass.METATABLE_NAME,
    superclassname = EventClass.METATABLE_NAME)
public class PlayerItemPickupEventClass extends
    ProxyingLuaClass<PlayerEvent.ItemPickupEvent, PlayerItemPickupEventClass.Proxy<PlayerEvent.ItemPickupEvent>> {
  public static final String METATABLE_NAME = "PlayerItemPickupEvent";

  @Override
  public String getMetatableName() {
    return METATABLE_NAME;
  }

  @Override
  public Proxy<PlayerEvent.ItemPickupEvent> toLua(PlayerEvent.ItemPickupEvent javaObj) {
    return new Proxy<>(getConverters(), getMetatable(), javaObj);
  }

  public static class Proxy<D extends PlayerEvent.ItemPickupEvent> extends EventClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addImmutable("player", getConverters().toLua(delegate.player));
      addImmutable("item", getConverters().toLua(delegate.pickedUp));
    }
  }
}
