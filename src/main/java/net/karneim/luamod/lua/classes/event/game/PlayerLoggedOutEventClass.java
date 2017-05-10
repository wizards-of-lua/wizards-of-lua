package net.karneim.luamod.lua.classes.event.game;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.sandius.rembulan.Table;

@LuaModule("PlayerLoggedOutEvent")
public class PlayerLoggedOutEventClass extends DelegatingLuaClass<PlayerLoggedOutEvent> {
  public PlayerLoggedOutEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends PlayerLoggedOutEvent> b,
      PlayerLoggedOutEvent delegate) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
