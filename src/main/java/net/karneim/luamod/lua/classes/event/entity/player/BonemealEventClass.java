package net.karneim.luamod.lua.classes.event.entity.player;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.sandius.rembulan.Table;

@LuaModule("BonemealEvent")
public class BonemealEventClass extends DelegatingLuaClass<BonemealEvent> {
  public BonemealEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends BonemealEvent> b, BonemealEvent d) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.addReadOnly("block", () -> repo.wrap(d.getBlock()));
    b.addReadOnly("pos", () -> repo.wrap(d.getPos()));
    b.addReadOnly("world", () -> repo.wrap(d.getWorld()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
