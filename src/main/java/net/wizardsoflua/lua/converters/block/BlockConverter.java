package net.wizardsoflua.lua.converters.block;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
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
import net.wizardsoflua.lua.table.PatchedImmutableTable;

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

  public WolBlock toJava(Object luaObj) {
    converters.getTypes().checkAssignable(METATABLE_NAME, luaObj, Terms.MANDATORY);
    Proxy proxy = (Proxy) luaObj;
    return proxy.delegate;
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
      PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();
      NbtConverter.insertValues(builder, data);
      return builder.build();
    }

    public Table getData(IBlockState blockState) {
      PatchedImmutableTable.Builder b = new PatchedImmutableTable.Builder();
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
