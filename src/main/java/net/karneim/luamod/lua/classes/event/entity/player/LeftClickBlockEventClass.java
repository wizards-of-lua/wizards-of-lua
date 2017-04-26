package net.karneim.luamod.lua.classes.event.entity.player;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.sandius.rembulan.Table;

@LuaModule("LeftClickBlockEvent")
public class LeftClickBlockEventClass extends ImmutableLuaClass<LeftClickBlockEvent> {
  public LeftClickBlockEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder b, LeftClickBlock event) {
    b.add("type", repo.wrap(getModuleName()));
    b.add("hitVec", repo.wrap(event.getHitVec()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
