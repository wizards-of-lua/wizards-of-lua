package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = PlayerLoggedInEventClass.NAME, superClass = EventClass.class)
public class PlayerLoggedInEventClass extends
    ProxyingLuaClass<PlayerEvent.PlayerLoggedInEvent, PlayerLoggedInEventClass.Proxy<PlayerEvent.PlayerLoggedInEvent>> {
  public static final String NAME = "PlayerLoggedInEvent";

  @Override
  public Proxy<PlayerEvent.PlayerLoggedInEvent> toLua(PlayerEvent.PlayerLoggedInEvent javaObj) {
    return new Proxy<>(getConverters(), getMetaTable(), javaObj);
  }

  public static class Proxy<D extends PlayerEvent.PlayerLoggedInEvent> extends EventClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addImmutable("player", getConverters().toLua(delegate.player));
    }
  }
}
