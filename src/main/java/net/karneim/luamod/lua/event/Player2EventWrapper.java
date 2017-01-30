package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.wrapper.EntityPlayerWrapper;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class Player2EventWrapper<E extends PlayerEvent> extends EventWrapper<E> {
  public Player2EventWrapper(@Nullable E event, EventType eventType) {
    super(event, eventType.name());
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    builder.add("player", new EntityPlayerWrapper(delegate.player).getLuaObject());
  }
}
