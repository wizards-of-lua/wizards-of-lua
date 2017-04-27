package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.sandius.rembulan.Table;

@LuaModule("PlayerLoggedInEvent")
public class PlayerLoggedInEventClass extends ImmutableLuaClass<PlayerLoggedInEvent> {
  public PlayerLoggedInEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder b, PlayerLoggedInEvent event) {
    b.add("type", repo.wrap(getModuleName()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
