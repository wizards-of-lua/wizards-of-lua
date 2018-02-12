package net.wizardsoflua.lua.classes.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyCachingLuaClass;

@DeclareLuaClass(name = DroppedItemClass.NAME, superClass = EntityClass.class)
public class DroppedItemClass
    extends ProxyCachingLuaClass<EntityItem, DroppedItemClass.Proxy<EntityItem>> {
  public static final String NAME = "DroppedItem";

  @Override
  public DroppedItemClass.Proxy<EntityItem> toLua(EntityItem delegate) {
    return new Proxy<>(new EntityApi<>(this, delegate));
  }

  public static class Proxy<D extends EntityItem> extends EntityProxy<EntityApi<D>, D> {
    public Proxy(EntityApi<D> api) {
      super(api);
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
