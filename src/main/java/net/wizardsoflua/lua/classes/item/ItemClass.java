package net.wizardsoflua.lua.classes.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyCachingLuaClass;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.function.NamedFunction2;
import net.wizardsoflua.lua.nbt.NbtConverter;

@DeclareLuaClass(name = ItemClass.NAME)
public class ItemClass extends ProxyCachingLuaClass<ItemStack, ItemClass.Proxy> {
  public static final String NAME = "Item";

  public ItemClass() {
    add(new PutNbtFunction());
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
      add("damage", delegate::getItemDamage, this::setDamage);
      add("repairCost", this::getRepairCost, this::setRepairCost);
      add("count", delegate::getCount, this::setCount);
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

    private void setDisplayName(Object luaObject) {
      String displayName = getConverters().toJava(String.class, luaObject, "displayName");
      delegate.setStackDisplayName(displayName);
    }

    private void setDamage(Object luaObject) {
      int damage = getConverters().toJava(Integer.class, luaObject, "damage");
      delegate.setItemDamage(damage);
    }

    private void setRepairCost(Object luaObject) {
      int repairCost = getConverters().toJava(Integer.class, luaObject, "repairCost");
      delegate.setRepairCost(repairCost);
    }

    private Object getRepairCost() {
      int cost = delegate.getRepairCost();
      return getConverters().toLua(cost);
    }

    private void setCount(Object luaObj) {
      int count = getConverters().toJava(Integer.class, luaObj, "count");
      delegate.setCount(count);
    }

    public void putNbt(Table nbt) {
      NBTTagCompound oldNbt = delegate.serializeNBT();
      NBTTagCompound newNbt = converters.getNbtConverter().merge(oldNbt, nbt);
      delegate.deserializeNBT(newNbt);
    }
  }

  private class PutNbtFunction extends NamedFunction2 {
    @Override
    public String getName() {
      return "putNbt";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2) {
      Proxy proxy = castToProxy(arg1);
      Table nbt = getConverters().toJava(Table.class, arg2, 2, "nbt", getName());
      proxy.putNbt(nbt);
      context.getReturnBuffer().setTo();
    }
  }
}
