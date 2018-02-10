package net.wizardsoflua.lua.classes.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyCachingLuaClass;

@DeclareLuaClass(name = DroppedItemClass.NAME, superClass = EntityClass.class)
public class DroppedItemClass
    extends ProxyCachingLuaClass<EntityItem, DroppedItemClass.Proxy<EntityItem>> {
  public static final String NAME = "DroppedItem";

  @Override
  public DroppedItemClass.Proxy<EntityItem> toLua(EntityItem delegate) {
    return new Proxy<>(getConverters(), getMetaTable(), delegate);
  }

  public static class Proxy<D extends EntityItem> extends EntityClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
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
