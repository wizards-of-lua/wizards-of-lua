package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.nbt.NBTTagUtil;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.karneim.luamod.lua.wrapper.ItemStackInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

@LuaClass("ItemStack")
public class ItemStackClass extends AbstractLuaType {
  public ItemStackInstance newInstance(ItemStack delegate) {
    return new ItemStackInstance(getRepo(), delegate,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {
    Table metatable = Metatables.get(getRepo().getEnv(), getTypeName());
    metatable.rawset("getNbt", new GetNbtFunction());
    metatable.rawset("putNbt", new PutNbtFunction());
  }

  private class GetNbtFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("table expected but got nil!"));
      }
      ItemStack delegate = DelegatingTableWrapper.getDelegate(ItemStack.class, arg1);
      
      NBTTagCompound tagCompound = delegate.writeToNBT(new NBTTagCompound());
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

  private class PutNbtFunction extends AbstractFunction2 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("table expected but got nil!"));
      }
      ItemStack delegate = DelegatingTableWrapper.getDelegate(ItemStack.class, arg1);
      if (arg2 == null) {
        throw new IllegalArgumentException(String.format("table expected but got nil!"));
      }
      if (!(arg2 instanceof Table)) {
        throw new IllegalArgumentException(String.format("table expected but got %s", arg2));
      }
      Table data = (Table) arg2;

      NBTTagCompound origTag = delegate.writeToNBT(new NBTTagCompound());
      NBTTagCompound mergedTag = NBTTagUtil.merge(origTag, data);
      delegate.readFromNBT(mergedTag);
      
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
