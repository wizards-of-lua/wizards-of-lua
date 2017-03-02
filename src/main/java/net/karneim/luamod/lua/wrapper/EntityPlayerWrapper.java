package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.LuaPlayer;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameType;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class EntityPlayerWrapper extends EntityLivingBaseWrapper<EntityPlayer> {
  public EntityPlayerWrapper(Table env, @Nullable EntityPlayer delegate) {
    super(env, delegate);
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
      builder.add("gamemode", new EnumWrapper(env, e).getLuaObject());
      builder.addNullable("getInventory", new GetInventoryFunction(mp));
    }
    builder.setMetatable(LuaPlayer.META_TABLE(env));
  }

  private class GetInventoryFunction extends AbstractFunction1 {
    private EntityPlayerMP mp;

    GetInventoryFunction(EntityPlayerMP mp) {
      this.mp = mp;
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("number expected but got nil!"));
      }
      if (!(arg1 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("number expected but got %s", arg1.getClass().getSimpleName()));
      }
      int index = ((Number) (arg1)).intValue();

      ItemStack itemStack = mp.inventory.getStackInSlot(index);
      ItemStackWrapper wrapper = new ItemStackWrapper(env, itemStack);
      context.getReturnBuffer().setTo(wrapper.getLuaObject());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
}
