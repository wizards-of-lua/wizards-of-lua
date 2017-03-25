package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableTableWrapper;
import net.minecraft.util.math.BlockPos;
import net.sandius.rembulan.Table;

public class BlockPosInstance extends ImmutableTableWrapper<BlockPos> {
  public BlockPosInstance(Table env, @Nullable BlockPos delegate, Table metatable) {
    super(env, delegate, metatable);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder builder) {
    builder.add("x", delegate.getX());
    builder.add("y", delegate.getY());
    builder.add("z", delegate.getZ());
  }
}
