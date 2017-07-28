package net.karneim.luamod.lua.classes.event.entity.living;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.sandius.rembulan.Table;

@LuaModule("LivingAttackEvent")
public class LivingAttackEventClass extends DelegatingLuaClass<LivingAttackEvent> {
  public LivingAttackEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends LivingAttackEvent> b,
      LivingAttackEvent delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.addReadOnly("amount", () -> repo.wrap(delegate.getAmount()));
    b.addReadOnly("source", () -> repo.wrap(delegate.getSource()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
