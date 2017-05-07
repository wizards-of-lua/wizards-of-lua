package net.karneim.luamod.lua.classes.entity.player;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.karneim.luamod.lua.util.LuaPreconditions.checkType;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameType;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

@LuaModule("Player")
public class EntityPlayerClass extends DelegatingLuaClass<EntityPlayer> {
  public EntityPlayerClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends EntityPlayer> b,
      EntityPlayer delegate) {
    EntityPlayerWrapper d = new EntityPlayerWrapper(delegate);
    // delegate.getBedLocation()
    // delegate.getFoodStats().getFoodLevel()
    // delegate.getFoodStats().getSaturationLevel()
    // delegate.getInventoryEnderChest()

    if (delegate instanceof EntityPlayerMP) {
      EntityPlayerMP mp = (EntityPlayerMP) delegate;
      b.add("gamemode", () -> repo.wrap(mp.interactionManager.getGameType()), d::setGameMode);
    }
    // delegate.getAbsorptionAmount();
    // delegate.getBedLocation()
    // delegate.getInventoryEnderChest()

    b.add("foodLevel", () -> delegate.getFoodStats().getFoodLevel(), d::setFoodLevel);
    b.add("foodSaturationLevel", () -> delegate.getFoodStats().getSaturationLevel(),
        d::setSaturationLevel);
  }

  private static class EntityPlayerWrapper {
    private final EntityPlayer delegate;

    public EntityPlayerWrapper(EntityPlayer delegate) {
      this.delegate = checkNotNull(delegate, "delegate == null!");
    }

    private void setFoodLevel(Object arg) {
      int value = ((Number) arg).intValue();
      delegate.getFoodStats().setFoodLevel(value);
    }

    private void setSaturationLevel(Object arg) {
      float value = ((Number) arg).floatValue();
      delegate.getFoodStats().setFoodSaturationLevel(value);
    }

    private void setGameMode(Object arg) {
      GameType mode = GameType.valueOf(String.valueOf(arg));
      EntityPlayerMP mp = (EntityPlayerMP) delegate;
      mp.setGameType(mode);
    }
  }

  @Override
  protected void addFunctions(Table luaClass) {
    luaClass.rawset("getInventory", new GetInventoryFunction());
  }

  private class GetInventoryFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(0, arg1, DelegatingTable.class);
      EntityPlayer delegate = checkType(0, self.getDelegate(), EntityPlayer.class);

      int index = checkType(1, arg2, Number.class).intValue();

      ItemStack itemStack = delegate.inventory.getStackInSlot(index);
      context.getReturnBuffer().setTo(repo.wrap(itemStack));
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
}
