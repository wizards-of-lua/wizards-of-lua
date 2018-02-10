package net.wizardsoflua.lua.classes.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyCachingLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = DroppedItemClass.NAME, superClass = EntityClass.class)
public class DroppedItemClass
    extends ProxyCachingLuaClass<EntityItem, DroppedItemClass.Proxy<EntityItem>> {
  public static final String NAME = "DroppedItem";

  @Override
  public DroppedItemClass.Proxy<EntityItem> toLua(EntityItem delegate) {
    return new Proxy<>(this, delegate);
  }

  public static class Proxy<D extends EntityItem> extends EntityClass.Proxy<D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(luaClass, delegate);
      add("item", this::getItem, this::setItem);
    }

    public Object getItem() {
      ItemStack stack = delegate.getEntityItem();
      return getConverters().toLua(stack);
    }

    public void setItem(Object luaObj) {
      ItemStack stack = getConverters().toJava(ItemStack.class, luaObj, "item");
      delegate.setEntityItemStack(stack);
    }
  }
}
