package net.karneim.luamod.lua.classes.stats;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.stats.StatBase;
import net.sandius.rembulan.Table;

@LuaModule("StatBase")
public class StatBaseClass extends DelegatingLuaClass<StatBase> {
  public StatBaseClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends StatBase> b, StatBase d) {
    b.addReadOnly("statId", () -> repo.wrap(d.statId));
    b.addReadOnly("statName", () -> repo.wrap(d.getStatName()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
