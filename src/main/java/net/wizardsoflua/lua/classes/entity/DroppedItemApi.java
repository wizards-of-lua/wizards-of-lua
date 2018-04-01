package net.wizardsoflua.lua.classes.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

/**
 * The <span class="notranslate">DroppedItem</span> class represents things that are lying somewhere
 * and can be collected by players.
 */
@GenerateLuaClass(name = DroppedItemApi.NAME)
@GenerateLuaDoc(subtitle = "Things That are Lying Around")
public class DroppedItemApi<D extends EntityItem> extends EntityApi<D> {
  public static final String NAME = "DroppedItem";

  public DroppedItemApi(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }

  /**
   * This is the [item](/modules/Item/) that has been dropped.
   */
  @LuaProperty
  public ItemStack getItem() {
    return delegate.getItem();
  }

  @LuaProperty
  public void setItem(ItemStack item) {
    delegate.setItem(item);
  }
}
