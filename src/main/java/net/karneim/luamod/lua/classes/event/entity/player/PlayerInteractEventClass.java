package net.karneim.luamod.lua.classes.event.entity.player;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.Table;

@LuaModule("PlayerInteractEvent")
public class PlayerInteractEventClass extends ImmutableLuaClass<PlayerInteractEvent> {
  public PlayerInteractEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder b, PlayerInteractEvent event) {
    b.add("type", repo.wrap(getModuleName()));
    b.add("hand", repo.wrap(event.getHand()));
    b.add("item", repo.wrap(event.getItemStack()));
    b.add("pos", repo.wrap(event.getPos()));
    b.add("face", repo.wrap(event.getFace()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
