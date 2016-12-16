package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.wrapper.EntityPlayerWrapper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.ImmutableTable;

public class PlayerEventWrapper<E extends PlayerEvent> extends EventWrapper<E> {
  public PlayerEventWrapper(@Nullable E event, EventType eventType) {
    super(event, eventType.name());
  }

  @Override
  protected void addProperties(ImmutableTable.Builder builder) {
    super.addProperties(builder);
    builder.add("player", new EntityPlayerWrapper(delegate.getEntityPlayer()).getLuaObject());
  }
}
