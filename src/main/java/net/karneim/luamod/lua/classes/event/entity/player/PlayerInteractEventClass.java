package net.karneim.luamod.lua.classes.event.entity.player;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.Table;

@LuaModule("PlayerInteractEvent")
public class PlayerInteractEventClass extends DelegatingLuaClass<PlayerInteractEvent> {
  public PlayerInteractEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b, PlayerInteractEvent event) {
    b.add("hand", repo.wrap(event.getHand()));
    b.add("item", repo.wrap(event.getItemStack()));
    b.add("pos", repo.wrap(event.getPos()));
    b.add("face", repo.wrap(event.getFace()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
