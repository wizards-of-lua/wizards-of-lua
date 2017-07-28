package net.karneim.luamod.lua.classes.event.entity.living;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.sandius.rembulan.Table;

@LuaModule("SpecialSpawnEvent")
public class SpecialSpawnEventClass extends DelegatingLuaClass<SpecialSpawn> {
  public SpecialSpawnEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends SpecialSpawn> b,
      SpecialSpawn delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
