package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.sandius.rembulan.Table;

@LuaModule("PlayerLoggedOutEvent")
public class PlayerLoggedOutEventClass extends ImmutableLuaClass<PlayerLoggedOutEvent> {
  public PlayerLoggedOutEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder b, PlayerLoggedOutEvent event) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
