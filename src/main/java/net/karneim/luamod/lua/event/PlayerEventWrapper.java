package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.wrapper.EntityPlayerWrapper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.sandius.rembulan.Table;

public class PlayerEventWrapper<E extends PlayerEvent> extends EventWrapper<E> {
  public PlayerEventWrapper(Table env, @Nullable E event, EventType eventType) {
    super(env, event, eventType.name());
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    builder.addNullable("player", new EntityPlayerWrapper(env, delegate.getEntityPlayer()).getLuaObject());
  }
}
