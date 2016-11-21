package net.karneim.luamod.lua.mcwrapper;

import net.karneim.luamod.lua.LuaWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;

public class ItemStackWrapper extends LuaWrapper<ItemStack> {
  public ItemStackWrapper(ItemStack delegate) {
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
    ResourceLocation registryName = delegate.getItem().getRegistryName();
    return registryName.getResourceDomain() + ":" + registryName.getResourcePath();
  }
}
