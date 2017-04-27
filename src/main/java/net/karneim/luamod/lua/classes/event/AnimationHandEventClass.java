package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.event.AnimationHandEvent;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.sandius.rembulan.Table;

@LuaModule("AnimationHandEvent")
public class AnimationHandEventClass extends ImmutableLuaClass<AnimationHandEvent> {
  public AnimationHandEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder b, AnimationHandEvent event) {
    b.add("type", repo.wrap(getModuleName()));
    b.add("hand", repo.wrap(event.getHand()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
