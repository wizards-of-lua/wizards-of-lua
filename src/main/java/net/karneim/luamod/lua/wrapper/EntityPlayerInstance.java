package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.sandius.rembulan.Table;

public class EntityPlayerInstance extends EntityLivingBaseInstance<EntityPlayer> {

  public EntityPlayerInstance(Table env, @Nullable EntityPlayer delegate, Table metatable) {
    super(env, delegate, metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    // delegate.getBedLocation()
    // delegate.getFoodStats().getFoodLevel()
    // delegate.getFoodStats().getSaturationLevel()
    // delegate.getInventoryEnderChest()

    if (delegate instanceof EntityPlayerMP) {
      EntityPlayerMP mp = (EntityPlayerMP) delegate;
      GameType e = mp.interactionManager.getGameType();
      builder.add("gamemode", new EnumInstance(env, e).getLuaObject());
    }
  }

}
