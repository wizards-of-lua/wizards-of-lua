package net.karneim.luamod.lua.classes.event.brewing;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.sandius.rembulan.Table;

@LuaModule("PotionBrewPostEvent")
public class PotionBrewPostEventClass extends DelegatingLuaClass<PotionBrewEvent.Post> {
  public PotionBrewPostEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends PotionBrewEvent.Post> b,
      PotionBrewEvent.Post delegate) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
