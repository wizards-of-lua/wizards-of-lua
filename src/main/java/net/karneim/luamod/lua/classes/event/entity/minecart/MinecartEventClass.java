package net.karneim.luamod.lua.classes.event.entity.minecart;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraftforge.event.entity.minecart.MinecartEvent;
import net.sandius.rembulan.Table;

@LuaModule("MinecartEvent")
public class MinecartEventClass extends DelegatingLuaClass<MinecartEvent> {
  public MinecartEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends MinecartEvent> b,
      MinecartEvent delegate) {
    b.addReadOnly("type", () -> repo.wrap(getModuleName()));
    b.addReadOnly("minecart", () -> repo.wrap(delegate.getMinecart()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
