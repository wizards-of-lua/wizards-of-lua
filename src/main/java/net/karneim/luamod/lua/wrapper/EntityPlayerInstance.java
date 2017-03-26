package net.karneim.luamod.lua.wrapper;

import static net.karneim.luamod.lua.wrapper.WrapperFactory.wrap;

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
  protected void addProperties(DelegatingTable.Builder b) {
    super.addProperties(b);
    // delegate.getBedLocation()
    // delegate.getFoodStats().getFoodLevel()
    // delegate.getFoodStats().getSaturationLevel()
    // delegate.getInventoryEnderChest()

    if (delegate instanceof EntityPlayerMP) {
      EntityPlayerMP mp = (EntityPlayerMP) delegate;
      b.add("gamemode", () -> wrap(env, mp.interactionManager.getGameType()), this::setGameMode);
      
     
    }
    // delegate.getAbsorptionAmount();
    // delegate.getBedLocation()
    // delegate.getInventoryEnderChest()
    
    b.add("foodLevel", () -> delegate.getFoodStats().getFoodLevel(), this::setFoodLevel);
    b.add("foodSaturationLevel", () -> delegate.getFoodStats().getSaturationLevel(), this::setSaturationLevel);
  }

  private void setFoodLevel(Object arg) {
    int value = ((Number)arg).intValue();
    delegate.getFoodStats().setFoodLevel(value);
  }
  
  private void setSaturationLevel(Object arg) {
    float value = ((Number)arg).floatValue();
    delegate.getFoodStats().setFoodSaturationLevel(value);
  }
  
  private void setGameMode(Object arg) {
    GameType mode = GameType.valueOf(String.valueOf(arg));
    EntityPlayerMP mp = (EntityPlayerMP) delegate;
    mp.setGameType(mode);
  }
}
