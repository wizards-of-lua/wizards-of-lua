package net.karneim.luamod.lua.classes.event.entity.player;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.sandius.rembulan.Table;

@LuaModule("LeftClickBlockEvent")
public class LeftClickBlockEventClass extends DelegatingLuaClass<LeftClickBlock> {
  public LeftClickBlockEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends LeftClickBlock> b,
      LeftClickBlock delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.addReadOnly("hitVec", () -> repo.wrap(delegate.getHitVec()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
