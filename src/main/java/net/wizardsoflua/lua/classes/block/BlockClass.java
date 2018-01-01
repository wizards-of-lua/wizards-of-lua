package net.wizardsoflua.lua.classes.block;

import java.util.Collection;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.block.WolBlock;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;
import net.wizardsoflua.lua.classes.common.DelegatingProxy;
import net.wizardsoflua.lua.nbt.NbtConverter;
import net.wizardsoflua.lua.table.PatchedImmutableTable;

@DeclareLuaClass(name = BlockClass.METATABLE_NAME)
public class BlockClass extends ProxyingLuaClass<WolBlock, BlockClass.Proxy<WolBlock>> {
  public static final String METATABLE_NAME = "Block";

  public BlockClass() {
    add("withData", new WithDataFunction());
    add("withNbt", new WithNbtFunction());
    add("asItem", new AsItemFunction());
  }

  @Override
  public String getMetatableName() {
    return METATABLE_NAME;
  }

  @Override
  public Proxy<WolBlock> toLua(WolBlock javaObj) {
    return new Proxy<>(getConverters(), getMetatable(), javaObj);
  }

  public static class Proxy<D extends WolBlock> extends DelegatingProxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addReadOnly("name", this::getName);
      addReadOnly("material", this::getMaterial);
      addImmutable("data", getData());
      if (delegate.getNbt() != null) {
        addImmutable("nbt", getNbt());
      }
    }

    @Override
    public boolean isTransferable() {
      return true;
    }

    private String getName() {
      ResourceLocation name = delegate.getBlockState().getBlock().getRegistryName();
      if ("minecraft".equals(name.getResourceDomain())) {
        return name.getResourcePath();
      } else {
        return name.toString();
      }
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
        Comparable<?> value = blockState.getValue(name);
        Object luaValue = BlockPropertyConverter.toLua(value);
        b.add(name.getName(), luaValue);
      }
      return b.build();
    }

    private Object getNbt() {
      NBTTagCompound nbt = delegate.getNbt();
      if (nbt == null) {
        return null;
      }
      return NbtConverter.toLua(nbt);
    }
  }

  private class WithDataFunction extends AbstractFunction2 {
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      WolBlock oldWolBlock = getConverters().toJava(WolBlock.class, arg1);
      Table dataTable = getConverters().castToTable(arg2);

      IBlockState state = oldWolBlock.getBlockState();
      for (IProperty<?> key : state.getPropertyKeys()) {
        Object luaValue = dataTable.rawget(key.getName());
        if (luaValue != null) {
          Comparable javaValue = BlockPropertyConverter.toJava(key.getValueClass(), luaValue);
          state = state.withProperty((IProperty) key, javaValue);
        }
      }

      WolBlock newWolBlock = new WolBlock(state, oldWolBlock.getNbt());
      Object result = getConverters().toLua(newWolBlock);
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
      WolBlock oldWolBlock = getConverters().toJava(WolBlock.class, arg1);
      Table nbtTable = getConverters().castToTable(arg2);

      NBTTagCompound oldNbt = oldWolBlock.getNbt();
      NBTTagCompound newNbt;
      if (oldNbt != null) {
        newNbt = converters.getNbtConverter().merge(oldNbt, nbtTable);
      } else {
        // newNbt = oldNbt;
        throw new IllegalArgumentException(String.format("Can't set nbt for block '%s'",
            oldWolBlock.getBlockState().getBlock().getRegistryName().getResourcePath()));
      }

      WolBlock newWolBlock = new WolBlock(oldWolBlock.getBlockState(), newNbt);
      Object result = getConverters().toLua(newWolBlock);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class AsItemFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      WolBlock wolBlock = getConverters().toJava(WolBlock.class, arg1);
      int amount =
          getConverters().toJavaOptional(Number.class, arg2, "amount").orElse(1).intValue();
      ItemStack itemStack = wolBlock.asItemStack(amount);
      Object result = getConverters().toLua(itemStack);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
}
