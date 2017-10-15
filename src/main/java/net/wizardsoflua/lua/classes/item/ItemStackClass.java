package net.wizardsoflua.lua.classes.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.InstanceCachingLuaClass;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.nbt.NbtConverter;
import net.wizardsoflua.lua.table.PatchedImmutableTable;

@DeclareLuaClass(name = ItemStackClass.METATABLE_NAME)
public class ItemStackClass extends InstanceCachingLuaClass<ItemStack> {
  public static final String METATABLE_NAME = "ItemStack";

  public ItemStackClass() {
    super(ItemStack.class);
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
      add("repairCost", delegate::getRepairCost, this::setRepairCost);
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
      NBTTagCompound data = delegate.getTagCompound();
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

    private void setStackSize(Object luaObj) {
      int value = getConverters().toJava(Integer.class, luaObj);
      delegate.setCount(value);
    }
  }

}
