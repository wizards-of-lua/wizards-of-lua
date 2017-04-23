package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.karneim.luamod.lua.wrapper.EntityPlayerInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

@LuaModule("Player")
public class EntityPlayerClass extends AbstractLuaType {
  public EntityPlayerInstance newInstance(EntityPlayer delegate) {
    return new EntityPlayerInstance(getRepo(), delegate,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {
    Table metatable = Metatables.get(getRepo().getEnv(), getTypeName());
    metatable.rawset("getInventory", new GetInventoryFunction());
  }

  private class GetInventoryFunction extends AbstractFunction2 {

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
      EntityPlayerMP delegate = DelegatingTableWrapper.getDelegate(EntityPlayerMP.class, arg1);
      int index = ((Number) (arg2)).intValue();

      ItemStack itemStack = delegate.inventory.getStackInSlot(index);
      DelegatingTable result =
          getRepo().get(ItemStackClass.class).newInstance(itemStack).getLuaObject();

      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
