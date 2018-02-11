package net.wizardsoflua.scribble;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class LuaItemProxy<A extends ItemApi<D>, D extends ItemStack> extends LuaApiProxy<A, D> {
  public LuaItemProxy(A api) {
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
