package net.karneim.luamod.lua.classes.event.game;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.sandius.rembulan.Table;

@LuaModule("PlayerRespawnEvent")
public class PlayerRespawnEventClass extends DelegatingLuaClass<PlayerRespawnEvent> {
  public PlayerRespawnEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends PlayerRespawnEvent> b,
      PlayerRespawnEvent delegate) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
