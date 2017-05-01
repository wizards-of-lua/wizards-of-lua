package net.karneim.luamod.lua.classes.entity.item;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.sandius.rembulan.Table;

@LuaModule("EntityMinecartCommandBlock")
public class EntityMinecartCommandBlockClass
    extends DelegatingLuaClass<EntityMinecartCommandBlock> {
  public EntityMinecartCommandBlockClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends EntityMinecartCommandBlock> b,
      EntityMinecartCommandBlock delegate) {
    b.addReadOnly("commandBlock", () -> repo.wrap(delegate.getCommandBlockLogic()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
