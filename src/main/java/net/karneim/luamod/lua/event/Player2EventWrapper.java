package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.wrapper.EntityPlayerWrapper;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.sandius.rembulan.Table;

public class Player2EventWrapper<E extends PlayerEvent> extends EventWrapper<E> {
  public Player2EventWrapper(Table env, @Nullable E event, EventType eventType) {
    super(env, event, eventType.name());
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    builder.addNullable("player", new EntityPlayerWrapper(env, delegate.player).getLuaObject());
  }
}
