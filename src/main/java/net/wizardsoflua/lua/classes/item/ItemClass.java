package net.wizardsoflua.lua.classes.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.InstanceCachingLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;
import net.wizardsoflua.lua.classes.common.LuaInstance;
import net.wizardsoflua.lua.extension.api.function.NamedFunction2;
import net.wizardsoflua.lua.nbt.NbtConverter;

@DeclareLuaClass(name = ItemClass.NAME)
public class ItemClass extends InstanceCachingLuaClass<ItemStack, ItemClass.Proxy> {
  public static final String NAME = "Item";

  @Override
  protected void onLoad() {
    add(new PutNbtFunction());
  }

  @Override
  public Proxy toLua(ItemStack delegate) {
    return new Proxy(this, delegate);
  }

  public static class Proxy extends LuaInstance<ItemStack> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, ItemStack delegate) {
      super(luaClass, delegate);
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
      NBTTagCompound newNbt = getConverters().getNbtConverter().merge(oldNbt, nbt);
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
      Proxy proxy = getConverters().toJava(Proxy.class, arg1, 1, "self", getName());
      Table nbt = getConverters().toJava(Table.class, arg2, 2, "nbt", getName());
      proxy.putNbt(nbt);
      context.getReturnBuffer().setTo();
    }
  }
}
