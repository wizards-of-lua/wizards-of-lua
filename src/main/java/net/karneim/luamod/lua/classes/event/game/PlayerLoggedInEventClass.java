package net.karneim.luamod.lua.classes.event.game;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.sandius.rembulan.Table;

@LuaModule("PlayerLoggedInEvent")
public class PlayerLoggedInEventClass extends DelegatingLuaClass<PlayerLoggedInEvent> {
  public PlayerLoggedInEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends PlayerLoggedInEvent> b,
      PlayerLoggedInEvent delegate) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
