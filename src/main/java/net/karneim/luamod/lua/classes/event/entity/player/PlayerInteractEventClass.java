package net.karneim.luamod.lua.classes.event.entity.player;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.Table;

@LuaModule("PlayerInteractEvent")
public class PlayerInteractEventClass extends DelegatingLuaClass<PlayerInteractEvent> {
  public PlayerInteractEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends PlayerInteractEvent> b,
      PlayerInteractEvent delegate) {
    b.addReadOnly("hand", () -> repo.wrap(delegate.getHand()));
    b.addReadOnly("item", () -> repo.wrap(delegate.getItemStack()));
    b.addReadOnly("pos", () -> repo.wrap(delegate.getPos()));
    b.addReadOnly("face", () -> repo.wrap(delegate.getFace()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
