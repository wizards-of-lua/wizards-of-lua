package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.entity.EntityLivingBase;
import net.sandius.rembulan.Table;


public class EntityLivingBaseWrapper<E extends EntityLivingBase> extends EntityWrapper<E> {
  public EntityLivingBaseWrapper(Table env, @Nullable E delegate) {
    super(env, delegate);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    // delegate.getAbsorptionAmount();
    // delegate.getBedLocation()
    // delegate.getFoodStats().getFoodLevel()
    // delegate.getFoodStats().getSaturationLevel()
    // delegate.getInventoryEnderChest()
    builder.addNullable("armor", new ArmorWrapper(env, delegate.getArmorInventoryList()).getLuaObject());
    builder.add("health", delegate.getHealth());
    builder.addNullable("mainHand", new ItemStackWrapper(env, delegate.getHeldItemMainhand()).getLuaObject());
    builder.addNullable("offHand", new ItemStackWrapper(env, delegate.getHeldItemOffhand()).getLuaObject());
  }

}
