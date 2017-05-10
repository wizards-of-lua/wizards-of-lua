package net.karneim.luamod.lua.classes.entity.item;

import static net.karneim.luamod.lua.util.LuaPreconditions.checkType;
import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeDelegatingTable;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.sandius.rembulan.Table;

@LuaModule("EntityMinecart")
public class EntityMinecartClass extends DelegatingLuaClass<EntityMinecart> {
  public EntityMinecartClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends EntityMinecart> b, EntityMinecart delegate) {
    b.add("currentCartSpeedCapOnRail", () -> repo.wrap(delegate.getCurrentCartSpeedCapOnRail()),
        o -> delegate.setCurrentCartSpeedCapOnRail(checkType(o, Number.class).floatValue()));
    b.add("displayTile", () -> repo.wrap(delegate.getDisplayTile()),
        o -> delegate.setDisplayTile(checkTypeDelegatingTable(o, IBlockState.class)));
    b.add("displayTileOffset", () -> repo.wrap(delegate.getDisplayTileOffset()),
        o -> delegate.setDisplayTileOffset(checkType(o, Number.class).intValue()));
    b.addReadOnly("maxCartSpeedOnRail", () -> repo.wrap(delegate.getMaxCartSpeedOnRail()));
    b.add("maxSpeedAirLateral", () -> repo.wrap(delegate.getMaxSpeedAirLateral()),
        o -> delegate.setMaxSpeedAirLateral(checkType(o, Number.class).floatValue()));
    b.add("maxSpeedAirVertical", () -> repo.wrap(delegate.getMaxSpeedAirVertical()),
        o -> delegate.setMaxSpeedAirVertical(checkType(o, Number.class).floatValue()));
    b.addReadOnly("type", () -> repo.wrap(delegate.getType()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
