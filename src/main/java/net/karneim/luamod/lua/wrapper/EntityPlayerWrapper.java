package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameType;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class EntityPlayerWrapper extends EntityLivingBaseWrapper<EntityPlayer> {

  private static final String CLASSNAME = "Player";
  public static final String MODULE = "net.karneim.luamod.lua.classes." + CLASSNAME;

  public static LuaFunction NEW(Table env) {
    Table metatable = Metatables.get(env, CLASSNAME);
    LuaFunction result = (LuaFunction) metatable.rawget("new");
    return result;
  }

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

    Table metatable = Metatables.get(env, CLASSNAME);
    builder.setMetatable(metatable);
    // FIXME die metatable enthält nicht die funktionen aus EntityWrapper
    // weil hier ja eine neue erzeugt wird.
    // Besser wäre ein add.
    // Aber noch besser wäre, wenn diese Functionen hier nicht andauernd die Metatable
    // überschreiben würden, wenn eine Instanz der Klasser gebaut wird.
    // Diese Methoden müssten VOR denen aus der Lua Code eingefügt werden,
    // und das auch nur EINMAL JE KLASSE.
    if (delegate instanceof EntityPlayerMP) {
      EntityPlayerMP mp = (EntityPlayerMP) delegate;
      GameType e = mp.interactionManager.getGameType();
      builder.add("gamemode", new EnumWrapper(env, e).getLuaObject());

      // builder.addNullable("getInventory", new GetInventoryFunction(mp));
      metatable.rawset("getInventory", new GetInventoryFunction());
    }
  }

  private class GetInventoryFunction extends AbstractFunction2 {

    GetInventoryFunction() {}

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("table expected but got nil!"));
      }
      if (arg2 == null) {
        throw new IllegalArgumentException(String.format("number expected but got nil!"));
      }
      if (!(arg2 instanceof Number)) {
        throw new IllegalArgumentException(
            String.format("number expected but got %s", arg2.getClass().getSimpleName()));
      }
      EntityPlayerMP delegate = getDelegate(EntityPlayerMP.class, arg1);
      int index = ((Number) (arg2)).intValue();

      ItemStack itemStack = delegate.inventory.getStackInSlot(index);
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
