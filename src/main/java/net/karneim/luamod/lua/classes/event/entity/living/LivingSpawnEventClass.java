package net.karneim.luamod.lua.classes.event.entity.living;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.sandius.rembulan.Table;

@LuaModule("LivingSpawnEvent")
public class LivingSpawnEventClass extends DelegatingLuaClass<LivingSpawnEvent> {
  public LivingSpawnEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b, PlayerRespawnEvent delegate) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
