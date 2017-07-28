package net.karneim.luamod.lua.classes.event.entity.living;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.sandius.rembulan.Table;

@LuaModule("LivingDeathEvent")
public class LivingDeathEventClass extends DelegatingLuaClass<LivingDeathEvent> {
  public LivingDeathEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends LivingDeathEvent> b,
      LivingDeathEvent delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.addReadOnly("source", () -> repo.wrap(delegate.getSource()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
