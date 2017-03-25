package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.NBTTagUtil;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.karneim.luamod.lua.wrapper.ItemStackInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class ItemStackClass {

  private final String classname = "ItemStack";
  public final String module = "net.karneim.luamod.lua.classes." + classname;

  private static final ItemStackClass SINGLETON = new ItemStackClass();

  public static ItemStackClass get() {
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

  public ItemStackInstance newInstance(Table env, ItemStack delegate) {
    return new ItemStackInstance(env, delegate, Metatables.get(env, classname));
  }

  private void addFunctions(Table env) {
    Table metatable = Metatables.get(env, classname);
    metatable.rawset("getInventory", new GetDataFunction());
  }

  private class GetDataFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("table expected but got nil!"));
      }
      ItemStack delegate = DelegatingTableWrapper.getDelegate(ItemStack.class, arg1);
      NBTTagCompound tagCompound = delegate.getTagCompound();
      PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();
      if (tagCompound != null) {
        NBTTagUtil.insertValues(builder, tagCompound);
      }
      PatchedImmutableTable tbl = builder.build();

      context.getReturnBuffer().setTo(tbl);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
