package net.karneim.luamod.lua.classes.event.entity.player;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.sandius.rembulan.Table;

@LuaModule("EntityItemPickupEvent")
public class EntityItemPickupEventClass extends DelegatingLuaClass<EntityItemPickupEvent> {
  public EntityItemPickupEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends EntityItemPickupEvent> b,
      EntityItemPickupEvent d) {
    b.addReadOnly("item", () -> repo.wrap(d.getItem()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
