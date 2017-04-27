package net.karneim.luamod.lua.classes.event.game;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.sandius.rembulan.Table;

@LuaModule("PlayerGameEvent")
public class PlayerGameEventClass extends DelegatingLuaClass<PlayerEvent> {
  public PlayerGameEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends PlayerEvent> b,
      PlayerEvent event) {
    b.addReadOnly("player", () -> repo.wrap(event.player));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
