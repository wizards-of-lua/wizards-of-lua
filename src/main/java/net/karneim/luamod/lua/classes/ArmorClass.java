package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.ArmorInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

@TypeName("Armor")
@ModulePackage(Constants.MODULE_PACKAGE)
public class ArmorClass extends AbstractLuaType {

  public void installInto(ChunkLoader loader, DirectCallExecutor executor, StateContext state)
      throws LoaderException, CallException, CallPausedException, InterruptedException {
    LuaFunction classFunc = loader.loadTextChunk(new Variable(getRepo().getEnv()), getTypeName(),
        String.format("require \"%s\"", getModule()));
    executor.call(state, classFunc);
  }

  public ArmorInstance newInstance(Iterable<ItemStack> delegate) {
    return new ArmorInstance(getRepo(), delegate,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

}
