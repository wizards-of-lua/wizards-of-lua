package net.karneim.luamod.lua.classes.event.entity.living;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.sandius.rembulan.Table;

@LuaModule("LivingEntityUseItemStopEvent")
public class LivingEntityUseItemStopEventClass
    extends DelegatingLuaClass<LivingEntityUseItemEvent.Stop> {
  public LivingEntityUseItemStopEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends LivingEntityUseItemEvent.Stop> b,
      LivingEntityUseItemEvent.Stop delegate) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
