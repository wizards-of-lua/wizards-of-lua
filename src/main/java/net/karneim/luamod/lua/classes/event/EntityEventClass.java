package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.minecraftforge.event.entity.EntityEvent;
import net.sandius.rembulan.Table;

@LuaModule("EntityEvent")
public class EntityEventClass extends ImmutableLuaClass<EntityEvent> {
  public EntityEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder b, EntityEvent event) {
    b.add("entity", repo.wrap(event.getEntity()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
