package net.wizardsoflua.scribble;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class LuaItemProxy extends LuaApiProxy<LuaItem, ItemStack> {
  public LuaItemProxy(LuaItem api) {
    super(api);
    add("damage", this::getDamage, this::setDamage);
    addReadOnly("nbt", this::getNbt);
  }

  private int getDamage() {
    return api.getDamage();
  }

  private void setDamage(Object luaObject) {
    int damage = getConverters().toJava(int.class, luaObject, "damage");
    api.setDamage(damage);
  }

  private Object getNbt() {
    NBTTagCompound result = api.getNbt();
    return getConverters().toLuaNullable(result);
  }
}
