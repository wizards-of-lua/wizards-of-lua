package net.karneim.luamod.lua.classes.event.entity.minecart;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.minecart.MinecartCollisionEvent;
import net.sandius.rembulan.Table;

@LuaModule("MinecartCollisionEvent")
public class MinecartCollisionEventClass extends DelegatingLuaClass<MinecartCollisionEvent> {
  public MinecartCollisionEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends MinecartCollisionEvent> b,
      MinecartCollisionEvent delegate) {
    b.addReadOnly("collider", () -> repo.wrap(delegate.getCollider()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
