package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.wrapper.EntityPlayerWrapper;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.sandius.rembulan.Table;

public class Player2EventWrapper<E extends PlayerEvent> extends EventWrapper<E> {
  public Player2EventWrapper(@Nullable E event, EventType eventType) {
    super(event, eventType.name());
  }

  @Override
  protected Table toLuaObject() {
    Table result = super.toLuaObject();
    result.rawset("player", new EntityPlayerWrapper(delegate.player).getLuaObject());
    return result;
  }
}
