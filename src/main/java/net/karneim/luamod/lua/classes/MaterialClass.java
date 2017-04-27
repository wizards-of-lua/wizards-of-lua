package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.patched.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.block.material.Material;
import net.sandius.rembulan.Table;

@LuaModule("Material")
public class MaterialClass extends DelegatingLuaClass<Material> {
  public MaterialClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b, Material delegate) {
    b.add("blocksLight", repo.wrap(delegate.blocksLight()));
    b.add("blocksMovement", repo.wrap(delegate.blocksMovement()));
    b.add("canBurn", repo.wrap(delegate.getCanBurn()));
    b.add("isLiquid", repo.wrap(delegate.isLiquid()));
    b.add("isOpaque", repo.wrap(delegate.isOpaque()));
    b.add("isSolid", repo.wrap(delegate.isSolid()));
    b.add("isToolNotRequired", repo.wrap(delegate.isToolNotRequired()));
    b.add("mobility", repo.wrap(delegate.getMobilityFlag()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
