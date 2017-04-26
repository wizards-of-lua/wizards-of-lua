package net.karneim.luamod.lua.classes.event.game;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.sandius.rembulan.Table;

@LuaModule("PlayerGameEvent")
public class PlayerGameEventClass extends ImmutableLuaClass<PlayerGameEvent> {
  public PlayerGameEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder b, PlayerEvent event) {
    b.add("type", repo.wrap(getModuleName()));
    b.add("player", repo.wrap(event.player));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
