package net.karneim.luamod.lua.wrapper;

import static net.karneim.luamod.lua.wrapper.WrapperFactory.wrap;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.ArmorClass;
import net.karneim.luamod.lua.classes.ItemStackClass;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.entity.EntityLivingBase;
import net.sandius.rembulan.Table;


public class EntityLivingBaseInstance<E extends EntityLivingBase> extends EntityInstance<E> {
  public EntityLivingBaseInstance(Table env, @Nullable E delegate, Table metatable) {
    super(env, delegate, metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b) {
    super.addProperties(b);
    b.addNullable("armor",
        ArmorClass.get().newInstance(env, delegate.getArmorInventoryList()).getLuaObject());
    b.addNullable("mainHand",
        ItemStackClass.get().newInstance(env, delegate.getHeldItemMainhand()).getLuaObject());
    b.addNullable("offHand",
        ItemStackClass.get().newInstance(env, delegate.getHeldItemOffhand()).getLuaObject());
    b.add("health", delegate::getHealth, this::setHealth);
  }
  
  private void setHealth(Object arg) {
    float value = ((Number) arg).floatValue();
    delegate.setHealth(value);
  }

}
