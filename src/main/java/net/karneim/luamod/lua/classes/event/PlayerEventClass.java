package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.sandius.rembulan.Table;

@LuaModule("PlayerEvent")
public class PlayerEventClass extends ImmutableLuaClass<PlayerEvent> {
  public PlayerEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder b, PlayerEvent event) {
    b.add("type", repo.wrap(getModuleName()));
    b.add("player", repo.wrap(event.getEntityPlayer()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
