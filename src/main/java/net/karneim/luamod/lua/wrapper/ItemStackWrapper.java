package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.sandius.rembulan.impl.ImmutableTable;

public class ItemStackWrapper extends StructuredLuaWrapper<ItemStack> {
  public ItemStackWrapper(@Nullable ItemStack delegate) {
    super(delegate);
  }

  @Override
  protected void toLuaObject(ImmutableTable.Builder builder) {
    super.toLuaObject(builder);
    builder.add("displayName", delegate.getDisplayName());
    builder.add("damage", delegate.getItemDamage());
    builder.add("name", getName());
  }

  private String getName() {
    Item item = delegate.getItem();
    if (item == null)
      return null;
    ResourceLocation registryName = item.getRegistryName();
    return registryName.getResourceDomain() + ":" + registryName.getResourcePath();
  }
}
