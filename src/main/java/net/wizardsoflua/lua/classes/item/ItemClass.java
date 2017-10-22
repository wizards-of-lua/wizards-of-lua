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
import net.wizardsoflua.lua.classes.InstanceCachingLuaClass;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.nbt.NbtConverter;
import net.wizardsoflua.lua.table.PatchedImmutableTable;

@DeclareLuaClass(name = ItemClass.METATABLE_NAME)
public class ItemClass extends InstanceCachingLuaClass<ItemStack> {
  public static final String METATABLE_NAME = "Item";

  public ItemClass() {
    super(ItemStack.class);
    add("putNbt", new PutNbtFunction());
  }

  @Override
  public Table toLua(ItemStack delegate) {
    return new Proxy(getConverters(), getMetatable(), delegate);
  }

  @Override
  public ItemStack toJava(Table luaObj) {
    Proxy proxy = getProxy(luaObj);
    return proxy.delegate;
  }

  protected Proxy getProxy(Object luaObj) {
    getConverters().getTypes().checkAssignable(METATABLE_NAME, luaObj);
    return (Proxy) luaObj;
  }

  public static class Proxy extends DelegatingProxy {

    private final ItemStack delegate;

    public Proxy(Converters converters, Table metatable, ItemStack delegate) {
      super(converters, metatable, delegate);
      this.delegate = delegate;

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
      NBTTagCompound data = delegate.serializeNBT();
      if (data == null) {
        return null;
      }
      PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();
      NbtConverter.insertValues(builder, data);
      return builder.build();
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
      NBTTagCompound newNbt = NbtConverter.merge(oldNbt, nbtTable);
      delegate.deserializeNBT(newNbt);
    }
  }

  private class PutNbtFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) {
      Proxy proxy = getProxy(arg1);
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
