package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;

public class ItemStackWrapper extends LuaWrapper<ItemStack> {
  public ItemStackWrapper(@Nullable ItemStack delegate) {
    super(delegate);
  }

  @Override
  protected Table toLuaObject() {
    DefaultTable result = new DefaultTable();
    result.rawset("displayName", delegate.getDisplayName());
    result.rawset("damage", delegate.getItemDamage());
    result.rawset("name", getName());
    return result;
  }

  private String getName() {
    Item item = delegate.getItem();
    if (item == null)
      return null;
    ResourceLocation registryName = item.getRegistryName();
    return registryName.getResourceDomain() + ":" + registryName.getResourcePath();
  }
}
