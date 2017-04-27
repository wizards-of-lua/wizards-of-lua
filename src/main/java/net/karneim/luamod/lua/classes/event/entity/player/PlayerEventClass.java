package net.karneim.luamod.lua.classes.event.entity.player;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.sandius.rembulan.Table;

@LuaModule("PlayerEvent")
public class PlayerEventClass extends DelegatingLuaClass<PlayerEvent> {
  public PlayerEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b, PlayerEvent event) {
    b.add("player", repo.wrap(event.getEntityPlayer()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
