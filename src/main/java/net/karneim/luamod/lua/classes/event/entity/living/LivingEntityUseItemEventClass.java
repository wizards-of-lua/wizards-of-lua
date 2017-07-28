package net.karneim.luamod.lua.classes.event.entity.living;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.sandius.rembulan.Table;

@LuaModule("LivingEntityUseItemEvent")
public class LivingEntityUseItemEventClass extends DelegatingLuaClass<LivingEntityUseItemEvent> {
  public LivingEntityUseItemEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends LivingEntityUseItemEvent> b,
      LivingEntityUseItemEvent delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.addReadOnly("duration", () -> repo.wrap(delegate.getDuration()));
    b.addReadOnly("item", () -> repo.wrap(delegate.getItem()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
