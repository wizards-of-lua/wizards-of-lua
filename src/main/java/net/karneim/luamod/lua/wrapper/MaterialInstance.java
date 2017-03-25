package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.minecraft.block.material.Material;
import net.sandius.rembulan.Table;

public class MaterialInstance extends DelegatingTableWrapper<Material> {
  public MaterialInstance(Table env, @Nullable Material delegate, Table metatable) {
    super(env, delegate, metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    builder.add("blocksLight", delegate.blocksLight());
    builder.add("blocksMovement", delegate.blocksMovement());
    builder.add("canBurn", delegate.getCanBurn());
    builder.add("isLiquid", delegate.isLiquid());
    builder.add("isOpaque", delegate.isOpaque());
    builder.add("isSolid", delegate.isSolid());
    builder.add("isToolNotRequired", delegate.isToolNotRequired());
    builder.addNullable("mobility", new EnumInstance(env, delegate.getMobilityFlag()).getLuaObject());
  }

}
