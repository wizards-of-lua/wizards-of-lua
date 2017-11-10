package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = PlayerLoggedInEventClass.METATABLE_NAME,
    superclassname = EventClass.METATABLE_NAME)
public class PlayerLoggedInEventClass extends
    ProxyingLuaClass<PlayerEvent.PlayerLoggedInEvent, PlayerLoggedInEventClass.Proxy<PlayerEvent.PlayerLoggedInEvent>> {
  public static final String METATABLE_NAME = "PlayerLoggedInEvent";

  @Override
  public String getMetatableName() {
    return METATABLE_NAME;
  }

  @Override
  public Proxy<PlayerEvent.PlayerLoggedInEvent> toLua(PlayerEvent.PlayerLoggedInEvent javaObj) {
    return new Proxy<>(getConverters(), getMetatable(), javaObj);
  }

  public static class Proxy<D extends PlayerEvent.PlayerLoggedInEvent> extends EventClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addImmutable("player", getConverters().toLua(delegate.player));
    }
  }
}
