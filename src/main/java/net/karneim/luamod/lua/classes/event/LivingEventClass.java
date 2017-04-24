package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.sandius.rembulan.Table;

@LuaModule("LivingEvent")
public class LivingEventClass extends ImmutableLuaClass<LivingEvent> {
  public LivingEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder b, LivingEvent event) {
    // entityLiving is already mapped as entity by superclass EntityEventClass
    // b.add("entityLiving", repo.wrap(event.getEntityLiving());
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
