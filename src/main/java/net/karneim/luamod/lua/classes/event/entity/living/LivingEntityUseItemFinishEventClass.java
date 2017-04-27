package net.karneim.luamod.lua.classes.event.entity.living;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.sandius.rembulan.Table;

@LuaModule("LivingEntityUseItemFinishEvent")
public class LivingEntityUseItemFinishEventClass
    extends DelegatingLuaClass<LivingEntityUseItemEvent.Finish> {
  public LivingEntityUseItemFinishEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends LivingEntityUseItemEvent.Finish> b,
      LivingEntityUseItemEvent.Finish delegate) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
