package net.karneim.luamod.lua.classes.event.wol;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.event.AnimationHandEvent;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.sandius.rembulan.Table;

@LuaModule("AnimationHandEvent")
public class AnimationHandEventClass extends DelegatingLuaClass<AnimationHandEvent> {
  public AnimationHandEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends AnimationHandEvent> b,
      AnimationHandEvent delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.addReadOnly("hand", () -> repo.wrap(delegate.getHand()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
