package net.karneim.luamod.lua.classes.event.entity.player;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.sandius.rembulan.Table;

@LuaModule("RightClickBlockEvent")
public class RightClickBlockEventClass extends DelegatingLuaClass<RightClickBlock> {
  public RightClickBlockEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends RightClickBlock> b,
      RightClickBlock event) {
    b.addReadOnly("hitVec", () -> repo.wrap(event.getHitVec()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
