package net.karneim.luamod.lua.classes.event.entity.minecart;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.minecart.MinecartEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.sandius.rembulan.Table;

@LuaModule("MinecartEvent")
public class MinecartEventClass extends DelegatingLuaClass<MinecartEvent> {
  public MinecartEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b, PlayerRespawnEvent delegate) {}

  @Override
  protected void addFunctions(Table luaClass) {}
}
