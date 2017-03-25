package net.karneim.luamod.lua.wrapper;

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
  protected void addProperties(DelegatingTable.Builder builder) {
    builder.addNullable("displayName", delegate.getDisplayName());
    builder.addNullable("damage", delegate.getItemDamage());
    builder.addNullable("name", getName());
  }

  private String getName() {
    Item item = delegate.getItem();
    if (item == null)
      return null;
    ResourceLocation registryName = item.getRegistryName();
    return registryName.getResourceDomain() + ":" + registryName.getResourcePath();
  }

}
