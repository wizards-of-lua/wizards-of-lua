package net.karneim.luamod.lua.classes.event.entity.living;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.sandius.rembulan.Table;

@LuaModule("LivingEvent")
public class LivingEventClass extends DelegatingLuaClass<LivingEvent> {
  public LivingEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends LivingEvent> b,
      LivingEvent delegate) {
    // entityLiving is already mapped as entity by superclass EntityEventClass
    // b.addReadOnly("entityLiving", () -> repo.wrap(delegate.getEntityLiving());
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
