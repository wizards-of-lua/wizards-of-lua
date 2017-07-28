package net.karneim.luamod.lua.classes.event.entity.living;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.sandius.rembulan.Table;

@LuaModule("LivingSpawnEvent")
public class LivingSpawnEventClass extends DelegatingLuaClass<LivingSpawnEvent> {
  public LivingSpawnEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends LivingSpawnEvent> b,
      LivingSpawnEvent delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.addReadOnly("world", () -> repo.wrap(delegate.getWorld()));
    b.addReadOnly("x", () -> repo.wrap(delegate.getX()));
    b.addReadOnly("y", () -> repo.wrap(delegate.getY()));
    b.addReadOnly("z", () -> repo.wrap(delegate.getZ()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
