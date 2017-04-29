package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.block.material.Material;
import net.sandius.rembulan.Table;

@LuaModule("Material")
public class MaterialClass extends DelegatingLuaClass<Material> {
  public MaterialClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends Material> b, Material delegate) {
    b.addReadOnly("blocksLight", () -> repo.wrap(delegate.blocksLight()));
    b.addReadOnly("blocksMovement", () -> repo.wrap(delegate.blocksMovement()));
    b.addReadOnly("canBurn", () -> repo.wrap(delegate.getCanBurn()));
    b.addReadOnly("isLiquid", () -> repo.wrap(delegate.isLiquid()));
    b.addReadOnly("isOpaque", () -> repo.wrap(delegate.isOpaque()));
    b.addReadOnly("isSolid", () -> repo.wrap(delegate.isSolid()));
    b.addReadOnly("isToolNotRequired", () -> repo.wrap(delegate.isToolNotRequired()));
    b.addReadOnly("mobility", () -> repo.wrap(delegate.getMobilityFlag()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
