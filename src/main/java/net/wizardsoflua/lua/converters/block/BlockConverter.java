package net.wizardsoflua.lua.converters.block;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.block.WolBlock;
import net.wizardsoflua.lua.converters.Converters;
import net.wizardsoflua.lua.converters.common.DelegatingProxy;
import net.wizardsoflua.lua.converters.nbt.NbtConverter;
import net.wizardsoflua.lua.converters.nbt.NbtPrimitiveConverter;
import net.wizardsoflua.lua.module.types.Terms;
import net.wizardsoflua.lua.table.DefaultTableBuilder;

public class BlockConverter {
  public static final String METATABLE_NAME = "Block";

  private final Converters converters;
  private final Table metatable;

  public BlockConverter(Converters converters) {
    this.converters = converters;
    // TODO do declaration outside this class
    this.metatable = converters.getTypes().declare(METATABLE_NAME);
    metatable.rawset("withData", new WithDataFunction());
    metatable.rawset("withNbt", new WithNbtFunction());
  }

  public Table toLua(WolBlock delegate) {
    return new Proxy(converters, metatable, delegate);
  }

  /**
   * /lua b=spell.block; spell.pos=spell.pos+Vec3.from(0,1,0); b.properties.facing = "west";
   * spell.block = b
   * 
   * @param proxy
   * @param pos
   * @param world
   */
  public static void setBlock(BlockConverter.Proxy proxy, BlockPos pos, World world) {
    Table table = (Table) proxy;
    IBlockState state = proxy.delegate.getBlockState();
    // Block-State
    Table dataTable = (Table) table.rawget("data");
    if (dataTable != null) {
      for (IProperty<?> key : state.getPropertyKeys()) {
        Object luaValue = dataTable.rawget(key.getName());
        Class<?> vc = key.getValueClass();
        Comparable javaValue = NbtPrimitiveConverter.toJava(vc, luaValue);
        state = state.withProperty((IProperty) key, javaValue);
      }
    }
    world.setBlockState(pos, state);
    // Nbt
    TileEntity tileEntity = world.getTileEntity(pos);
    if (tileEntity != null) {

      NBTTagCompound origData = new NBTTagCompound();
      tileEntity.writeToNBT(origData);
      patch(tileEntity, origData);
      Table luaNbt = (Table) table.rawget("nbt");
      NBTTagCompound newData;
      if (luaNbt != null) {
        newData = NbtConverter.merge(origData, luaNbt);
      } else {
        newData = origData;
      }
      newData.setInteger("x", pos.getX());
      newData.setInteger("y", pos.getY());
      newData.setInteger("z", pos.getZ());
      tileEntity.readFromNBT(newData);
      tileEntity.markDirty();
      int flags = 3; // TODO why 3?
      world.notifyBlockUpdate(pos, state, state, flags);
    }
  }

  private static void patch(TileEntity tileEntity, NBTTagCompound origData) {
    if (tileEntity instanceof TileEntityShulkerBox) {
      if (origData.getTag("Items") == null) {
        origData.setTag("Items", new NBTTagList());
      }
    }
  }

  // public WolBlock toJava(Object luaObj) {
  // converters.getTypes().checkAssignable(BlockConverter.METATABLE_NAME, luaObj, Terms.MANDATORY);
  // Table table = (Table) luaObj;
  // String name = converters.stringToJava(table.rawget("name"));
  // Block blockIn = getBlockByName(name);
  // IProperty<?> properties = null;
  // BlockStateContainer bsc = new BlockStateContainer(blockIn, properties);
  // IBlockState blockState = bsc.getBaseState();
  // blockState.TileEntity tileEntity = null;
  // WolBlock result = new WolBlock(blockState, tileEntity);
  // }

  private Block getBlockByName(String blockName) {
    Block block = (Block) Block.REGISTRY.getObject(new ResourceLocation(blockName));
    return block;
  }



  public static class Proxy extends DelegatingProxy {

    private final WolBlock delegate;

    public Proxy(Converters converters, Table metatable, WolBlock delegate) {
      super(converters, metatable, delegate);
      this.delegate = delegate;
      addReadOnly("name",
          () -> delegate.getBlockState().getBlock().getRegistryName().getResourcePath());
      addReadOnly("material",
          () -> converters.materialToLua(delegate.getBlockState().getMaterial()));
      addImmutable("data", getData(delegate.getBlockState()));
      if (delegate.getNbt() != null) {
        addImmutable("nbt", getNbt(delegate.getNbt()));
      }
    }

    private @Nullable Table getNbt(@Nullable NBTTagCompound data) {
      if (data == null) {
        return null;
      }
      DefaultTableBuilder builder = new DefaultTableBuilder();
      NbtConverter.insertValues(builder, data);
      return builder.build();
    }

    public Table getData(IBlockState blockState) {
      DefaultTableBuilder b = new DefaultTableBuilder();
      Collection<IProperty<?>> names = blockState.getPropertyKeys();
      for (IProperty<?> name : names) {
        Object value = blockState.getValue(name);
        Object luaValue = NbtPrimitiveConverter.toLua(value);
        b.add(name.getName(), luaValue);
      }
      return b.build();
    }
  }

  private class WithDataFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      converters.getTypes().checkAssignable(METATABLE_NAME, arg1);
      Proxy wrapper = (Proxy) arg1;
      Table dataTable = converters.getTypes().castTable(arg2, Terms.MANDATORY);

      WolBlock oldWolBlock = wrapper.delegate;
      IBlockState state = oldWolBlock.getBlockState();
      for (IProperty<?> key : state.getPropertyKeys()) {
        Object luaValue = dataTable.rawget(key.getName());
        Class<?> vc = key.getValueClass();
        Comparable javaValue = NbtPrimitiveConverter.toJava(vc, luaValue);
        state = state.withProperty((IProperty) key, javaValue);
      }

      WolBlock newWolBlock = new WolBlock(state, oldWolBlock.getNbt());
      Table result = converters.blockToLua(newWolBlock);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class WithNbtFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      converters.getTypes().checkAssignable(METATABLE_NAME, arg1);
      Proxy wrapper = (Proxy) arg1;
      Table nbtTable = converters.getTypes().castTable(arg2, Terms.MANDATORY);

      WolBlock oldWolBlock = wrapper.delegate;
      NBTTagCompound oldNbt = oldWolBlock.getNbt();

      NBTTagCompound newNbt;
      if (oldNbt != null) {
        newNbt = NbtConverter.merge(oldNbt, nbtTable);
      } else {
        // newNbt = oldNbt;
        throw new IllegalArgumentException(String.format("Can't set nbt for block '%s'",
            oldWolBlock.getBlockState().getBlock().getRegistryName().getResourcePath()));
      }

      WolBlock newWolBlock = new WolBlock(oldWolBlock.getBlockState(), newNbt);
      Table result = converters.blockToLua(newWolBlock);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }


}
