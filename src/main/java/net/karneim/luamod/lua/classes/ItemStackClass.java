package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.nbt.NBTTagUtil;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.karneim.luamod.lua.wrapper.ItemStackInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

@TypeName("ItemStack")
@ModulePackage(Constants.MODULE_PACKAGE)
public class ItemStackClass extends AbstractLuaType {
  public ItemStackInstance newInstance(ItemStack delegate) {
    return new ItemStackInstance(getRepo(), delegate,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {
    Table metatable = Metatables.get(getRepo().getEnv(), getTypeName());
    metatable.rawset("getData", new GetDataFunction());
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
