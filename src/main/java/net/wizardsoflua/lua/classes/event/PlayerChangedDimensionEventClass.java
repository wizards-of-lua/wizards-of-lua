package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = PlayerChangedDimensionEventClass.METATABLE_NAME,
    superclassname = EventClass.METATABLE_NAME)
public class PlayerChangedDimensionEventClass extends
    ProxyingLuaClass<PlayerEvent.PlayerChangedDimensionEvent, PlayerChangedDimensionEventClass.Proxy<PlayerEvent.PlayerChangedDimensionEvent>> {
  public static final String METATABLE_NAME = "PlayerChangedDimensionEvent";

  @Override
  public String getMetatableName() {
    return METATABLE_NAME;
  }

  @Override
  public Proxy<PlayerEvent.PlayerChangedDimensionEvent> toLua(
      PlayerEvent.PlayerChangedDimensionEvent javaObj) {
    return new Proxy<>(getConverters(), getMetatable(), javaObj);
  }

  public static class Proxy<D extends PlayerEvent.PlayerChangedDimensionEvent>
      extends EventClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addImmutable("player", getConverters().toLua(delegate.player));
      addImmutable("from", getConverters().toLua(delegate.fromDim));
      addImmutable("to", getConverters().toLua(delegate.toDim));
    }
  }
}
