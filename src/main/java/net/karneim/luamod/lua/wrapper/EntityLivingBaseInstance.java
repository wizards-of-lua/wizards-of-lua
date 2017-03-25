package net.karneim.luamod.lua.wrapper;

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
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    // delegate.getAbsorptionAmount();
    // delegate.getBedLocation()
    // delegate.getFoodStats().getFoodLevel()
    // delegate.getFoodStats().getSaturationLevel()
    // delegate.getInventoryEnderChest()
    builder.addNullable("armor",
        ArmorClass.get().newInstance(env, delegate.getArmorInventoryList()).getLuaObject());
    builder.add("health", delegate.getHealth());
    builder.addNullable("mainHand",
        ItemStackClass.get().newInstance(env, delegate.getHeldItemMainhand()).getLuaObject());
    builder.addNullable("offHand",
        ItemStackClass.get().newInstance(env, delegate.getHeldItemOffhand()).getLuaObject());
  }

}
