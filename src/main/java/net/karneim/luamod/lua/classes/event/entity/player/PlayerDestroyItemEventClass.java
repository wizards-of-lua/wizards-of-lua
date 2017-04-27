package net.karneim.luamod.lua.classes.event.entity.player;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.sandius.rembulan.Table;

@LuaModule("PlayerDestroyItemEvent")
public class PlayerDestroyItemEventClass extends DelegatingLuaClass<PlayerDestroyItemEvent> {
  public PlayerDestroyItemEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b, PlayerRespawnEvent event) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
