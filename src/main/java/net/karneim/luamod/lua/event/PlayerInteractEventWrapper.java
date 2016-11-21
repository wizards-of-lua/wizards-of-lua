package net.karneim.luamod.lua.event;

import net.karneim.luamod.lua.mcwrapper.ItemStackWrapper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;

public class PlayerInteractEventWrapper extends EventWrapper<PlayerInteractEvent> {
  public PlayerInteractEventWrapper(PlayerInteractEvent delegate, EventType eventType) {
    super(delegate, eventType);
  }

  @Override
  protected Table toLuaObject() {
    Table result = new DefaultTable();
    result.rawset("hand", delegate.getHand().toString());
    result.rawset("item", new ItemStackWrapper(delegate.getItemStack()).getLuaObject());
    return result;
  }
}
