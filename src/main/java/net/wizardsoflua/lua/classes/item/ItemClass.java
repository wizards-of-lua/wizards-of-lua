package net.wizardsoflua.lua.classes.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyCachingLuaClass;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.nbt.NbtConverter;

@DeclareLuaClass(name = ItemClass.NAME)
public class ItemClass extends ProxyCachingLuaClass<ItemStack, ItemClass.Proxy> {
  public static final String NAME = "Item";

  public ItemClass() {
    add("putNbt", new PutNbtFunction());
  }

  @Override
  public Proxy toLua(ItemStack delegate) {
    return new Proxy(getConverters(), getMetaTable(), delegate);
  }

  public static class Proxy extends DelegatingProxy<ItemStack> {
    public Proxy(Converters converters, Table metatable, ItemStack delegate) {
      super(converters, metatable, delegate);
      addReadOnly("id", this::getId);
      add("displayName", delegate::getDisplayName, this::setDisplayName);
      add("damage", delegate::getItemDamage, this::setItemDamage);
      add("repairCost", this::getRepairCost, this::setRepairCost);
      add("count", delegate::getCount, this::setStackSize);
      addReadOnly("nbt", this::getNbt);
    }

    @Override
    public boolean isTransferable() {
      return true;
    }

    private String getId() {
      ResourceLocation name = delegate.getItem().getRegistryName();
      if ("minecraft".equals(name.getResourceDomain())) {
        return name.getResourcePath();
      } else {
        return name.toString();
      }
    }

    private Object getNbt() {
      NBTTagCompound nbt = delegate.serializeNBT();
      if (nbt == null) {
        return null;
      }
      return NbtConverter.toLua(nbt);
    }

    private void setDisplayName(Object luaObj) {
      delegate.setStackDisplayName(getConverters().toJava(String.class, luaObj));
    }

    private void setItemDamage(Object luaObj) {
      int value = getConverters().toJava(Integer.class, luaObj);
      delegate.setItemDamage(value);
    }

    private void setRepairCost(Object luaObj) {
      int value = getConverters().toJava(Integer.class, luaObj);
      delegate.setRepairCost(value);
    }

    private Object getRepairCost() {
      int cost = delegate.getRepairCost();
      return getConverters().toLua(cost);
    }

    private void setStackSize(Object luaObj) {
      int value = getConverters().toJava(Integer.class, luaObj);
      delegate.setCount(value);
    }

    public void putNbt(Object luaObj) {
      Table nbtTable = getConverters().castToTable(luaObj);
      NBTTagCompound oldNbt = delegate.serializeNBT();
      delegate.writeToNBT(oldNbt);
      NBTTagCompound newNbt = converters.getNbtConverter().merge(oldNbt, nbtTable);
      delegate.deserializeNBT(newNbt);
    }
  }

  private class PutNbtFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) {
      Proxy proxy = castToProxy(arg1);
      proxy.putNbt(arg2);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
}
