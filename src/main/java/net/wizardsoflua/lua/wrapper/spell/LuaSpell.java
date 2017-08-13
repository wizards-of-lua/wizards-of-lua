package net.wizardsoflua.lua.wrapper.spell;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.lib.StringLib;
import net.sandius.rembulan.runtime.AbstractFunctionAnyArg;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.wrapper.WrapperFactory;
import net.wizardsoflua.lua.wrapper.entity.LuaEntity;
import net.wizardsoflua.spell.SpellEntity;

public class LuaSpell {
  public static final String METATABLE_NAME = "Spell";

  private final WrapperFactory wrappers;
  private final Table metatable;

  public LuaSpell(WrapperFactory wrappers) {
    this.wrappers = wrappers;
    // TODO do declaration outside this class
    this.metatable = wrappers.getTypes().declare(METATABLE_NAME, LuaEntity.METATABLE_NAME);
    metatable.rawset("execute", new ExecuteFunction());
  }

  public Table wrap(SpellEntity delegate) {
    return new Wrapper(wrappers, metatable, delegate);
  }

  public static class Wrapper extends LuaEntity.Wrapper {

    private final SpellEntity delegate;

    public Wrapper(WrapperFactory wrappers, Table metatable, SpellEntity delegate) {
      super(wrappers, metatable, delegate);
      this.delegate = delegate;
      addReadOnly("owner", this::getOwner);
      addReadOnly("block", this::getBlock);
      add("visible", this::isVisible, this::setVisible);

      setMetatable(metatable);
    }

    public Table getOwner() {
      return getWrappers().wrap(delegate.getOwner());
    }

    public Table getBlock() {
      BlockPos pos = new BlockPos(delegate.getPositionVector());
      IBlockState blockState = delegate.getEntityWorld().getBlockState(pos);
      return getWrappers().wrap(blockState);
    }

    public void setVisible(Object luaObj) {
      boolean value =
          checkNotNull(getWrappers().unwrapBoolean(luaObj), "Expected boolean but got nil!");
      delegate.setVisible(value);
    }

    public boolean isVisible() {
      return delegate.isVisible();
    }

    public int execute(String command) {
      World world = delegate.getEntityWorld();
      return world.getMinecraftServer().getCommandManager().executeCommand(delegate, command);
    }
  }

  private class ExecuteFunction extends AbstractFunctionAnyArg {
    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      Object arg0 = args[0];
      wrappers.getTypes().checkAssignable(METATABLE_NAME, arg0);
      Wrapper wrapper = (Wrapper) arg0;


      LuaFunction formatFunc = StringLib.format();
      Object[] argArray = new Object[args.length - 1];
      System.arraycopy(args, 1, argArray, 0, args.length - 1);
      formatFunc.invoke(context, argArray);
      String command = String.valueOf(context.getReturnBuffer().get(0));

      int result = wrapper.execute(command);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
}
