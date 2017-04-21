package net.karneim.luamod.lua.wrapper;

import static net.karneim.luamod.lua.util.PreconditionsUtils.checkType;

import com.google.common.base.Preconditions;

import net.karneim.luamod.Blocks;
import net.karneim.luamod.lua.NBTTagUtil;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class BlocksWrapper {

  public static BlocksWrapper installInto(LuaTypesRepo repo, Blocks blocks) {
    BlocksWrapper result = new BlocksWrapper(blocks);
    repo.getEnv().rawset("Blocks", result.getLuaTable());
    return result;
  }

  private final Blocks blocks;
  private final Table luaTable = DefaultTable.factory().newTable();

  public BlocksWrapper(Blocks blocks) {
    this.blocks = Preconditions.checkNotNull(blocks);
    luaTable.rawset("getData", new GetDataFunction());
    luaTable.rawset("putData", new PutDataFunction());
  }

  public Table getLuaTable() {
    return luaTable;
  }

  /**
   * Returns the NBT-Data of the block at the given location
   */
  private class GetDataFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Vec3 expected but got nil!"));
      }
      Table vector = checkType(arg1, Table.class);
      Number x = checkType(vector.rawget("x"), Number.class);
      Number y = checkType(vector.rawget("y"), Number.class);
      Number z = checkType(vector.rawget("z"), Number.class);
      Vec3d v = new Vec3d(x.doubleValue(), y.doubleValue(), z.doubleValue());

      BlockPos pos = new BlockPos(v);
      NBTTagCompound tagCompound = blocks.getData(pos);

      PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();
      if (tagCompound != null) {
        NBTTagUtil.insertValues(builder, tagCompound);
      }
      PatchedImmutableTable tbl = builder.build();

      context.getReturnBuffer().setTo(tbl);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  /**
   * Merges the NBT-Data into the block at the given location
   */
  private class PutDataFunction extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Vec3 expected but got nil!"));
      }
      Table vector = checkType(arg1, Table.class);
      Number x = checkType(vector.rawget("x"), Number.class);
      Number y = checkType(vector.rawget("y"), Number.class);
      Number z = checkType(vector.rawget("z"), Number.class);
      Vec3d v = new Vec3d(x.doubleValue(), y.doubleValue(), z.doubleValue());

      if (arg2 == null) {
        throw new IllegalArgumentException(String.format("table expected but got nil!"));
      }
      if (!(arg2 instanceof Table)) {
        throw new IllegalArgumentException(String.format("table expected but got %s", arg2));
      }
      Table data = (Table) arg2;

      BlockPos pos = new BlockPos(v);
      NBTTagCompound origTag = blocks.getData(pos);
      if (origTag != null) {
        NBTTagCompound mergedTag = NBTTagUtil.merge(origTag, data);
        blocks.putData(pos, mergedTag);
        context.getReturnBuffer().setTo();
      } else {
        throw new IllegalArgumentException("Block at pos " + pos + " has no block data!");
      }
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
