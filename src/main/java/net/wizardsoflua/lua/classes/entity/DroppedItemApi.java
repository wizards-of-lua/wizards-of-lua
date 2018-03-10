package net.wizardsoflua.lua.classes.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@GenerateLuaClass(name = DroppedItemApi.NAME)
@GenerateLuaDoc(subtitle = "Things That are Lying Around")
public class DroppedItemApi<D extends EntityItem> extends EntityApi<D> {
  public static final String NAME = "DroppedItem2";

  public DroppedItemApi(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }

  @LuaProperty
  public ItemStack getItem() {
    return delegate.getEntityItem();
  }

  @LuaProperty
  public void setItem(ItemStack item) {
    delegate.setEntityItemStack(item);
  }
}
