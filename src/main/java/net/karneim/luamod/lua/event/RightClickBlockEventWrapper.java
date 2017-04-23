package net.karneim.luamod.lua.event;

import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.sandius.rembulan.Table;

public class RightClickBlockEventWrapper extends PlayerInteractEventWrapper<RightClickBlock> {

  public RightClickBlockEventWrapper(LuaTypesRepo repo, RightClickBlock delegate,
      EventType eventType, Table metatable) {
    super(repo, delegate, eventType, metatable);
    // TODO Auto-generated constructor stub
  }

}
