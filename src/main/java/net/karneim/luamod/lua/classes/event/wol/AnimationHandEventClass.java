package net.karneim.luamod.lua.classes.event.wol;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.event.AnimationHandEvent;
import net.karneim.luamod.lua.patched.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.sandius.rembulan.Table;

@LuaModule("AnimationHandEvent")
public class AnimationHandEventClass extends DelegatingLuaClass<AnimationHandEvent> {
  public AnimationHandEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b, AnimationHandEvent event) {
    b.add("hand", repo.wrap(event.getHand()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
