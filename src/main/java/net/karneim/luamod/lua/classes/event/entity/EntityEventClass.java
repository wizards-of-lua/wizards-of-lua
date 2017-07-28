package net.karneim.luamod.lua.classes.event.entity;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.EntityEvent;
import net.sandius.rembulan.Table;

@LuaModule("EntityEvent")
public class EntityEventClass extends DelegatingLuaClass<EntityEvent> {
  public EntityEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends EntityEvent> b,
      EntityEvent delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.addReadOnly("entity", () -> repo.wrap(delegate.getEntity()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
