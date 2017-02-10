package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.entity.EntityLivingBase;


public class EntityLivingBaseWrapper<E extends EntityLivingBase> extends EntityWrapper<E> {
  public EntityLivingBaseWrapper(@Nullable E delegate) {
    super(delegate);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    // delegate.getAbsorptionAmount();
    // delegate.getBedLocation()
    // delegate.getFoodStats().getFoodLevel()
    // delegate.getFoodStats().getSaturationLevel()
    // delegate.getInventoryEnderChest()
    builder.addNullable("armor", new ArmorWrapper(delegate.getArmorInventoryList()).getLuaObject());
    builder.add("health", delegate.getHealth());
    builder.addNullable("mainHand", new ItemStackWrapper(delegate.getHeldItemMainhand()).getLuaObject());
    builder.addNullable("offHand", new ItemStackWrapper(delegate.getHeldItemOffhand()).getLuaObject());
  }

}
