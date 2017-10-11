package net.wizardsoflua.lua.classes.block;

import java.util.Collection;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.block.WolBlock;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.nbt.NbtConverter;
import net.wizardsoflua.lua.nbt.NbtPrimitiveConverter;
import net.wizardsoflua.lua.table.PatchedImmutableTable;

public class BlockClass {
  public static final String METATABLE_NAME = "Block";

  private final Converters converters;
  private final Table metatable;

  public BlockClass(Converters converters) {
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
    Proxy proxy = getProxy(luaObj);
    return proxy.delegate;
  }

  protected Proxy getProxy(Object luaObj) {
    converters.getTypes().checkAssignable(METATABLE_NAME, luaObj);
    return (Proxy) luaObj;
  }

  public static class Proxy extends DelegatingProxy {

    private final WolBlock delegate;

    public Proxy(Converters converters, Table metatable, WolBlock delegate) {
      super(converters, metatable, delegate);
      this.delegate = delegate;
      addReadOnly("name", this::getName);
      addReadOnly("material", this::getMaterial);
      addImmutable("data", getData());
      if (delegate.getNbt() != null) {
        addImmutable("nbt", getNbt());
      }
    }

    private String getName() {
      return delegate.getBlockState().getBlock().getRegistryName().getResourcePath();
    }

    private Object getMaterial() {
      Material mat = delegate.getBlockState().getMaterial();
      return getConverters().toLua(mat);
    }

    private Object getData() {
      IBlockState blockState = delegate.getBlockState();
      PatchedImmutableTable.Builder b = new PatchedImmutableTable.Builder();
      Collection<IProperty<?>> names = blockState.getPropertyKeys();
      for (IProperty<?> name : names) {
        Object value = blockState.getValue(name);
        Object luaValue = NbtPrimitiveConverter.toLua(value);
        b.add(name.getName(), luaValue);
      }
      return b.build();
    }

    private Object getNbt() {
      NBTTagCompound data = delegate.getNbt();
      if (data == null) {
        return null;
      }
      PatchedImmutableTable.Builder builder = new PatchedImmutableTable.Builder();
      NbtConverter.insertValues(builder, data);
      return builder.build();
    }
  }

  private class WithDataFunction extends AbstractFunction2 {
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      WolBlock oldWolBlock = converters.toJava(WolBlock.class, arg1);
      Table dataTable = converters.castToTable(arg2);

      IBlockState state = oldWolBlock.getBlockState();
      for (IProperty<?> key : state.getPropertyKeys()) {
        Object luaValue = dataTable.rawget(key.getName());
        if (luaValue != null) {
          Class<?> vc = key.getValueClass();
          Comparable javaValue = NbtPrimitiveConverter.toJava(vc, luaValue);
          state = state.withProperty((IProperty) key, javaValue);
        }
      }

      WolBlock newWolBlock = new WolBlock(state, oldWolBlock.getNbt());
      Object result = converters.toLua(newWolBlock);
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
      WolBlock oldWolBlock = converters.toJava(WolBlock.class, arg1);
      Table nbtTable = converters.castToTable(arg2);

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
      Object result = converters.toLua(newWolBlock);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
