package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.LuaMod;
import net.karneim.luamod.lua.DynamicTable;
import net.karneim.luamod.lua.NBTTagUtil;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction0;
import net.sandius.rembulan.runtime.AbstractFunction3;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class ItemStackWrapper extends StructuredLuaWrapper<ItemStack> {
  public ItemStackWrapper(@Nullable ItemStack delegate) {
    super(delegate);
  }

  @Override
  protected void addProperties(DynamicTable.Builder builder) {
    super.addProperties(builder);
    builder.add("displayName", delegate.getDisplayName());
    builder.add("damage", delegate.getItemDamage());
    builder.add("name", getName());
    builder.add("spawn", new SpawnFunction());
    builder.add("getData", new GetDataFunction());
  }

  private String getName() {
    Item item = delegate.getItem();
    if (item == null)
      return null;
    ResourceLocation registryName = item.getRegistryName();
    return registryName.getResourceDomain() + ":" + registryName.getResourcePath();
  }

  private class GetDataFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      NBTTagCompound tagCompound = delegate.getTagCompound();
      DynamicTable.Builder builder = new DynamicTable.Builder(null);
      if ( tagCompound != null) {
        NBTTagUtil.insertValues(builder, tagCompound);
      }
      DynamicTable tbl = builder.build();

      context.getReturnBuffer().setTo(tbl);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class SpawnFunction extends AbstractFunction3 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2, Object arg3)
        throws ResolvedControlThrowable {
      if (arg1 == null || !(arg1 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("Integer value for dx expected but got %s!", arg1));
      }
      if (arg2 == null || !(arg2 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("Integer value for dy expected but got %s!", arg2));
      }
      if (arg3 == null || !(arg3 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("Integer value for dz expected but got %s!", arg3));
      }

      double x = ((Number) arg1).doubleValue();
      double y = ((Number) arg2).doubleValue();
      double z = ((Number) arg3).doubleValue();

      // TODO this is just the overworld. This won't work in the Nether. What to do?
      World world = LuaMod.instance.getServer().getEntityWorld();

      InventoryHelper.spawnItemStack(world, x, y, z, delegate);

      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  //
}
