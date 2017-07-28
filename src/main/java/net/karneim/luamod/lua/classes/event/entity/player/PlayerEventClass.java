package net.karneim.luamod.lua.classes.event.entity.player;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.sandius.rembulan.Table;

@LuaModule("PlayerEvent")
public class PlayerEventClass extends DelegatingLuaClass<PlayerEvent> {
  public PlayerEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends PlayerEvent> b,
      PlayerEvent delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.addReadOnly("player", () -> repo.wrap(delegate.getEntityPlayer()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
