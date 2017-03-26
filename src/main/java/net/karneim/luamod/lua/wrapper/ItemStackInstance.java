package net.karneim.luamod.lua.wrapper;

import static net.karneim.luamod.lua.wrapper.WrapperFactory.wrap;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.sandius.rembulan.Table;

public class ItemStackInstance extends DelegatingTableWrapper<ItemStack> {
  public ItemStackInstance(Table env, @Nullable ItemStack delegate, Table metatable) {
    super(env, delegate, metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b) {
    b.addNullable("name", getName());
    b.add("displayName", () -> delegate.getDisplayName(), this::setDisplayName);
    b.add("damage", () -> delegate.getItemDamage(), this::setItemDamage);
    b.add("repairCost", () -> delegate.getRepairCost(), this::setRepairCost);
    b.add("stackSize", () -> delegate.stackSize, this::setStackSize);
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
