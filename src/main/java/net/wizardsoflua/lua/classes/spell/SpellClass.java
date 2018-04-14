package net.wizardsoflua.lua.classes.spell;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sandius.rembulan.lib.StringLib;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.block.LiveWolBlock;
import net.wizardsoflua.block.WolBlock;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.InstanceCachingLuaClass;
import net.wizardsoflua.lua.classes.entity.EntityApi;
import net.wizardsoflua.lua.classes.entity.EntityClass;
import net.wizardsoflua.lua.classes.entity.EntityInstance;
import net.wizardsoflua.lua.function.NamedFunctionAnyArg;
import net.wizardsoflua.spell.SpellEntity;

@DeclareLuaClass(name = SpellClass.NAME, superClass = EntityClass.class)
public class SpellClass
    extends InstanceCachingLuaClass<SpellEntity, SpellClass.Proxy<SpellEntity>> {
  public static final String NAME = "Spell";

  @Override
  protected void onLoad() {
    add(new ExecuteFunction());
  }

  @Override
  public Proxy<SpellEntity> toLua(SpellEntity delegate) {
    return new Proxy<>(new EntityApi<>(this, delegate));
  }

  public static class Proxy<D extends SpellEntity> extends EntityInstance<EntityApi<D>, D> {
    public Proxy(EntityApi<D> api) {
      super(api);
      addReadOnly("owner", this::getOwner);
      add("block", this::getBlock, this::setBlock);
      add("visible", this::isVisible, this::setVisible);
      addReadOnly("sid", () -> delegate.getSid());
      addReadOnly("data", this::getData);
    }

    public @Nullable Object getOwner() {
      Entity ownerEntity = delegate.getOwnerEntity();
      return getConverter().toLuaNullable(ownerEntity);
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
      return getConverter().toLua(block);
    }

    public void setBlock(Object luaObj) {
      // WolBlock wolBlock = getConverters().toJava(WolBlock.class, luaObj);
      // World world = delegate.getEntityWorld();
      // BlockPos pos = new BlockPos(delegate.getPositionVector());
      // wolBlock.setBlock(world, pos);
      WolBlock block = getConverter().toJava(WolBlock.class, luaObj, "block");
      World world = delegate.getEntityWorld();
      BlockPos pos = new BlockPos(delegate.getPositionVector());
      block.setBlock(world, pos);
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
      boolean value = getConverter().toJava(Boolean.class, luaObj, "visible");
      delegate.setVisible(value);
    }

    public boolean isVisible() {
      return delegate.isVisible();
    }

    public Object getData() {
      return delegate.getData(classLoader);
    }

    public int execute(String command) {
      World world = delegate.getEntityWorld();
      return world.getMinecraftServer().getCommandManager().executeCommand(delegate, command);
    }
  }

  private class ExecuteFunction extends NamedFunctionAnyArg {
    @Override
    public String getName() {
      return "execute";
    }

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      Object arg0 = args[0];
      Proxy<?> proxy = getConverter().toJava(Proxy.class, arg0, 1, "self", getName());
      if (args.length < 2) {
        throw new IllegalArgumentException("Expected command, but got nil");
      }
      if (args.length == 2) {
        Object arg1 = args[1];
        String command = getConverter().toJava(String.class, arg1, 1, "command", getName());
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
  }
}
