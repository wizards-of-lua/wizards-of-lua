package net.karneim.luamod.lua.classes.event.entity.living;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.sandius.rembulan.Table;

@LuaModule("LivingEntityUseItemStartEvent")
public class LivingEntityUseItemStartEventClass
    extends DelegatingLuaClass<LivingEntityUseItemEvent.Start> {
  public LivingEntityUseItemStartEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends LivingEntityUseItemEvent.Start> b,
      LivingEntityUseItemEvent.Start delegate) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
