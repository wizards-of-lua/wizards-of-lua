package net.karneim.luamod.lua.classes;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.karneim.luamod.lua.util.PreconditionsUtils.checkType;

import net.karneim.luamod.lua.nbt.NBTTagUtil;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

@LuaModule("ItemStack")
public class ItemStackClass extends DelegatingLuaClass<ItemStack> {
  public ItemStackClass(Table env) {
    super(env);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<ItemStack> b, ItemStack delegate) {
    ItemStackWrapper d = new ItemStackWrapper(delegate);
    b.addNullable("name", d.getName());
    b.add("displayName", delegate::getDisplayName, d::setDisplayName);
    b.add("damage", delegate::getItemDamage, d::setItemDamage);
    b.add("repairCost", delegate::getRepairCost, d::setRepairCost);
    b.add("stackSize", () -> delegate.stackSize, d::setStackSize);
  }

  private static class ItemStackWrapper {
    private final ItemStack delegate;

    public ItemStackWrapper(ItemStack delegate) {
      this.delegate = checkNotNull(delegate, "delegate == null!");
    }

    private void setDisplayName(Object arg) {
      delegate.setStackDisplayName(String.valueOf(arg));
    }

    private void setItemDamage(Object arg) {
      int value = ((Number) arg).intValue();
      delegate.setItemDamage(value);
    }

    private void setRepairCost(Object arg) {
      int value = ((Number) arg).intValue();
      delegate.setRepairCost(value);
    }

    private void setStackSize(Object arg) {
      int value = ((Number) arg).intValue();
      delegate.stackSize = value;
    }

    private String getName() {
      Item item = delegate.getItem();
      if (item == null)
        return null;
      ResourceLocation registryName = item.getRegistryName();
      return registryName.getResourceDomain() + ":" + registryName.getResourcePath();
    }
  }

  @Override
  protected void addFunctions(Table luaClass) {
    luaClass.rawset("getNbt", new GetNbtFunction());
    luaClass.rawset("putNbt", new PutNbtFunction());
  }

  private class GetNbtFunction extends AbstractFunction1 {
    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(arg1, DelegatingTable.class);
      ItemStack delegate = checkType(self.getDelegate(), ItemStack.class);

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
      DelegatingTable<?> self = checkType(0, arg1, DelegatingTable.class);
      ItemStack delegate = checkType(0, self.getDelegate(), ItemStack.class);

      Table data = checkType(1, arg2, Table.class);

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
