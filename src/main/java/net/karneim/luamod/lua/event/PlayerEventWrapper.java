package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.wrapper.EntityPlayerWrapper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.sandius.rembulan.Table;

public class PlayerEventWrapper<E extends PlayerEvent> extends EventWrapper<E> {
  public PlayerEventWrapper(@Nullable E event, EventType eventType) {
    super(event, eventType);
  }

  @Override
  protected Table toLuaObject() {
    Table result = super.toLuaObject();
    result.rawset("player", new EntityPlayerWrapper(delegate.getEntityPlayer()).getLuaObject());
    return result;
  }
}
