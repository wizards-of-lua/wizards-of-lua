package net.wizardsoflua.scribble;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaModule;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.ObjectClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@LuaModule(name = "Item", superClass = ObjectClass.class)
public class LuaItem extends LuaApi<ItemStack> {
  public LuaItem(ProxyingLuaClass<?, ?> luaClass, ItemStack delegate) {
    super(luaClass, delegate);
  }

  @LuaProperty
  public int getItemDamage() {
    return delegate.getItemDamage();
  }

  @LuaProperty
  public void setItemDamage(int value) {
    delegate.setItemDamage(value);
  }

  @LuaProperty
  public NBTTagCompound getNbt() {
    return delegate.serializeNBT();
  }

  @LuaFunction
  public void putNbt(Table nbt) {
    NBTTagCompound oldNbt = delegate.serializeNBT();
    NBTTagCompound newNbt = getConverters().getNbtConverter().merge(oldNbt, nbt);
    delegate.deserializeNBT(newNbt);
  }
}
