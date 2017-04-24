package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.minecraft.block.material.Material;
import net.sandius.rembulan.Table;

@LuaModule("Material")
public class MaterialClass extends ImmutableLuaClass<Material> {
  public MaterialClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder b, Material delegate) {
    b.add("blocksLight", delegate.blocksLight());
    b.add("blocksMovement", delegate.blocksMovement());
    b.add("canBurn", delegate.getCanBurn());
    b.add("isLiquid", delegate.isLiquid());
    b.add("isOpaque", delegate.isOpaque());
    b.add("isSolid", delegate.isSolid());
    b.add("isToolNotRequired", delegate.isToolNotRequired());
    b.add("mobility", repo.wrap(delegate.getMobilityFlag()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
