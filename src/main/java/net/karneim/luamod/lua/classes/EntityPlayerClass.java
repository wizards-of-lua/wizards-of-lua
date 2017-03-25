package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.karneim.luamod.lua.wrapper.EntityPlayerInstance;
import net.karneim.luamod.lua.wrapper.ItemStackInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class EntityPlayerClass {

  private final String classname = "Player";
  public final String module = "net.karneim.luamod.lua.classes." + classname;

  private static final EntityPlayerClass SINGLETON = new EntityPlayerClass();

  public static EntityPlayerClass get() {
    return SINGLETON;
  }

  public void installInto(Table env, ChunkLoader loader, DirectCallExecutor executor,
      StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc =
        loader.loadTextChunk(new Variable(env), classname, String.format("require \"%s\"", module));
    executor.call(state, classFunc);
    addFunctions(env);
  }

  public EntityPlayerInstance newInstance(Table env, EntityPlayer delegate) {
    return new EntityPlayerInstance(env, delegate, Metatables.get(env, classname));
  }

  private void addFunctions(Table env) {
    Table metatable = Metatables.get(env, classname);
    metatable.rawset("getInventory", new GetInventoryFunction(env));
  }

  private static class GetInventoryFunction extends AbstractFunction2 {

    private Table env;

    GetInventoryFunction(Table env) {
      this.env = env;
    }

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
      DelegatingTable result = ItemStackClass.get().newInstance(env, itemStack).getLuaObject();
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
