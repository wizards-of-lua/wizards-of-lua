package net.wizardsoflua.lua.classes.spell;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.lib.StringLib;
import net.sandius.rembulan.runtime.AbstractFunctionAnyArg;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.block.LiveWolBlock;
import net.wizardsoflua.block.WolBlock;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyCachingLuaClass;
import net.wizardsoflua.lua.classes.entity.EntityClass;
import net.wizardsoflua.spell.SpellEntity;

@DeclareLuaClass(name = SpellClass.NAME, superClass = EntityClass.class)
public class SpellClass extends ProxyCachingLuaClass<SpellEntity, SpellClass.Proxy<SpellEntity>> {
  public static final String NAME = "Spell";

  public SpellClass() {
    add("execute", new ExecuteFunction());
  }

  @Override
  public Proxy<SpellEntity> toLua(SpellEntity delegate) {
    return new Proxy<>(getConverters(), getMetaTable(), delegate);
  }

  public static class Proxy<D extends SpellEntity> extends EntityClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addReadOnly("owner", this::getOwner);
      add("block", this::getBlock, this::setBlock);
      add("visible", this::isVisible, this::setVisible);
      addReadOnly("sid", () -> delegate.getSid());
    }

    public @Nullable Object getOwner() {
      Entity ownerEntity = delegate.getOwnerEntity();
      return getConverters().toLuaNullable(ownerEntity);
    }

    public Object getBlock() {
      // BlockPos pos = new BlockPos(delegate.getPositionVector());
      // IBlockState blockState = delegate.getEntityWorld().getBlockState(pos);
      // TileEntity tileEntity = delegate.getEntityWorld().getTileEntity(pos);
      // WolBlock block = new WolBlock(blockState, tileEntity);
      // return getConverters().toLua(block);
      BlockPos pos = new BlockPos(delegate.getPositionVector());
      World world = delegate.getEntityWorld();
      LiveWolBlock block = new LiveWolBlock(pos, world);
      return getConverters().toLua(block);
    }

    public void setBlock(Object luaObj) {
      // WolBlock wolBlock = getConverters().toJava(WolBlock.class, luaObj);
      // World world = delegate.getEntityWorld();
      // BlockPos pos = new BlockPos(delegate.getPositionVector());
      // wolBlock.setBlock(world, pos);
      WolBlock wolBlock = getConverters().toJava(WolBlock.class, luaObj);
      World world = delegate.getEntityWorld();
      BlockPos pos = new BlockPos(delegate.getPositionVector());
      wolBlock.setBlock(world, pos);
    }

    // public ItemStack getItemFromBlock() {
    // BlockPos pos = new BlockPos(delegate.getPositionVector());
    // IBlockState blockState = delegate.getEntityWorld().getBlockState(pos);
    // World world = delegate.getEntityWorld();
    // NBTTagCompound nbt = getNbt(world.getTileEntity(pos));
    // return ItemUtil.getItemStackFromBlock(blockState, nbt);
    // }
    //
    // private @Nullable NBTTagCompound getNbt(@Nullable TileEntity te) {
    // if (te != null) {
    // NBTTagCompound nbt = new NBTTagCompound();
    // te.writeToNBT(nbt);
    // nbt.removeTag("x");
    // nbt.removeTag("y");
    // nbt.removeTag("z");
    // return nbt;
    // }
    // return null;
    // }

    public void setVisible(Object luaObj) {
      boolean value = getConverters().toJava(Boolean.class, luaObj);
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
      Proxy<SpellEntity> proxy = castToProxy(arg0);
      if (args.length < 2) {
        throw new IllegalArgumentException("Expected command, but got nil");
      }
      if (args.length == 2) {
        Object arg1 = args[1];
        String command = getConverters().toJavaOld(String.class, arg1, "command");
        int result = proxy.execute(command);
        context.getReturnBuffer().setTo(result);
      } else if (args.length > 2) {
        // format the command
        LuaFunction formatFunc = StringLib.format();
        Object[] argArray = new Object[args.length - 1];
        System.arraycopy(args, 1, argArray, 0, args.length - 1);
        formatFunc.invoke(context, argArray);
        String command = String.valueOf(context.getReturnBuffer().get(0));

        int result = proxy.execute(command);
        context.getReturnBuffer().setTo(result);
      }
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
}
