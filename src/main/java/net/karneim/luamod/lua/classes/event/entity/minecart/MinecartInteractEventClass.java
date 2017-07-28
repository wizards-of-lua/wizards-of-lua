package net.karneim.luamod.lua.classes.event.entity.minecart;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.sandius.rembulan.Table;

@LuaModule("MinecartInteractEvent")
public class MinecartInteractEventClass extends DelegatingLuaClass<MinecartInteractEvent> {
  public MinecartInteractEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends MinecartInteractEvent> b,
      MinecartInteractEvent d) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.addReadOnly("hand", () -> repo.wrap(d.getHand()));
    b.addReadOnly("item", () -> repo.wrap(d.getItem()));
    b.addReadOnly("player", () -> repo.wrap(d.getPlayer()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
