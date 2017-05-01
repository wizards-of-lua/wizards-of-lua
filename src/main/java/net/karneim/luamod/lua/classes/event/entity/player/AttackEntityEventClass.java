package net.karneim.luamod.lua.classes.event.entity.player;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.sandius.rembulan.Table;

@LuaModule("AttackEntityEvent")
public class AttackEntityEventClass extends DelegatingLuaClass<AttackEntityEvent> {
  public AttackEntityEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends AttackEntityEvent> b, AttackEntityEvent d) {
    b.addReadOnly("target", () -> repo.wrap(d.getTarget()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
