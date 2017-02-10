package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.minecraft.block.material.Material;

public class MaterialWrapper extends DelegatingTableWrapper<Material> {
  public MaterialWrapper(@Nullable Material delegate) {
    super(delegate);
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
    builder.addNullable("mobility", new EnumWrapper(delegate.getMobilityFlag()).getLuaObject());
  }

}
