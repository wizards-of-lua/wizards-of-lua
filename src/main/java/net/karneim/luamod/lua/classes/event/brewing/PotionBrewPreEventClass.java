package net.karneim.luamod.lua.classes.event.brewing;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.sandius.rembulan.Table;

@LuaModule("PotionBrewPreEvent")
public class PotionBrewPreEventClass extends DelegatingLuaClass<PotionBrewEvent.Pre> {
  public PotionBrewPreEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends PotionBrewEvent.Pre> b,
      PotionBrewEvent.Pre delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
