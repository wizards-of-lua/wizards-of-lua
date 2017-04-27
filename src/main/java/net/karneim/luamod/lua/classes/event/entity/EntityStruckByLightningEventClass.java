package net.karneim.luamod.lua.classes.event.entity;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.sandius.rembulan.Table;

@LuaModule("EntityStruckByLightningEvent")
public class EntityStruckByLightningEventClass extends DelegatingLuaClass<EntityStruckByLightningEvent> {
  public EntityStruckByLightningEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b, PlayerRespawnEvent event) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
