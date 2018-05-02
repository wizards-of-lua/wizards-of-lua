//package net.wizardsoflua.lua.classes.block;
//
//import java.util.Collection;
//
//import net.minecraft.block.material.Material;
//import net.minecraft.block.properties.IProperty;
//import net.minecraft.block.state.IBlockState;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.util.ResourceLocation;
//import net.sandius.rembulan.Table;
//import net.sandius.rembulan.runtime.ExecutionContext;
//import net.sandius.rembulan.runtime.ResolvedControlThrowable;
//import net.wizardsoflua.block.ImmutableWolBlock;
//import net.wizardsoflua.block.WolBlock;
//import net.wizardsoflua.lua.classes.DeclareLuaClass;
//import net.wizardsoflua.lua.classes.DelegatorLuaClass;
//import net.wizardsoflua.lua.classes.common.LuaInstance;
//import net.wizardsoflua.lua.function.NamedFunction1;
//import net.wizardsoflua.lua.function.NamedFunction2;
//import net.wizardsoflua.lua.nbt.NbtConverter;
//import net.wizardsoflua.lua.table.PatchedImmutableTable;
//
//@AutoService(DeclaredLuaClass.class)
//@DeclareLuaClass (name = BlockClass.NAME)
//public class BlockClass extends DelegatorLuaClass<WolBlock, BlockClass.Proxy<WolBlock>> {
//  public static final String NAME = "Block";
//
//  @Override
//  protected void onLoad() {
//    add(new WithDataFunction());
//    add(new WithNbtFunction());
//    add(new CopyFunction());
//    add(new AsItemFunction());
//  }
//
//  @Override
//  public Proxy<WolBlock> toLua(WolBlock javaObj) {
//    return new Proxy<>(this, javaObj);
//  }
//
//  public static class Proxy<D extends WolBlock> extends LuaInstance<D> {
//    public Proxy(DelegatorLuaClass<?, ?> luaClass, D delegate) {
//      super(luaClass, delegate);
//      addReadOnly("name", this::getName);
//      addReadOnly("material", this::getMaterial);
//      addReadOnly("data", this::getData);
//      addReadOnly("nbt", this::getNbt);
//    }
//
//    @Override
//    public boolean isTransferable() {
//      return true;
//    }
//
//    private String getName() {
//      ResourceLocation name = delegate.getBlockState().getBlock().getRegistryName();
//      if ("minecraft".equals(name.getResourceDomain())) {
//        return name.getResourcePath();
//      } else {
//        return name.toString();
//      }
//    }
//
//    private Object getMaterial() {
//      Material mat = delegate.getBlockState().getMaterial();
//      return getConverters().toLua(mat);
//    }
//
//    private Object getData() {
//      IBlockState blockState = delegate.getBlockState();
//      PatchedImmutableTable.Builder b = new PatchedImmutableTable.Builder();
//      Collection<IProperty<?>> names = blockState.getPropertyKeys();
//      for (IProperty<?> name : names) {
//        Comparable<?> value = blockState.getValue(name);
//        Object luaValue = BlockPropertyConverter.toLua(value);
//        b.add(name.getName(), luaValue);
//      }
//      return b.build();
//    }
//
//    private Object getNbt() {
//      NBTTagCompound nbt = delegate.getNbt();
//      if (nbt == null) {
//        return null;
//      }
//      return NbtConverter.toLua(nbt);
//    }
//  }
//
//  private class CopyFunction extends NamedFunction1 {
//    @Override
//    public String getName() {
//      return "copy";
//    }
//
//    @Override
//    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
//      WolBlock self = getConverters().toJava(WolBlock.class, arg1, 1, "self", getName());
//      ImmutableWolBlock newWolBlock = new ImmutableWolBlock(self.getBlockState(), self.getNbt());
//      Object result = getConverters().toLua(newWolBlock);
//      context.getReturnBuffer().setTo(result);
//    }
//  }
//
//  private class WithDataFunction extends NamedFunction2 {
//    @Override
//    public String getName() {
//      return "withData";
//    }
//
//    @Override
//    public void invoke(ExecutionContext context, Object arg1, Object arg2)
//        throws ResolvedControlThrowable {
//      WolBlock self = getConverters().toJava(WolBlock.class, arg1, 1, "self", getName());
//      Table data = getConverters().toJava(Table.class, arg2, 2, "data", getName());
//
//      IBlockState state = self.getBlockState();
//      for (IProperty<?> key : state.getPropertyKeys()) {
//        Object luaValue = data.rawget(key.getName());
//        if (luaValue != null) {
//          state = withProperty(state, key, luaValue);
//        }
//      }
//
//      ImmutableWolBlock newWolBlock = new ImmutableWolBlock(state, self.getNbt());
//      Object result = getConverters().toLua(newWolBlock);
//      context.getReturnBuffer().setTo(result);
//    }
//
//    private <T extends Comparable<T>> IBlockState withProperty(IBlockState state, IProperty<T> key,
//        Object luaValue) {
//      T javaValue = BlockPropertyConverter.toJava(key.getValueClass(), luaValue);
//      return state.withProperty(key, javaValue);
//    }
//  }
//
//  private class WithNbtFunction extends NamedFunction2 {
//    @Override
//    public String getName() {
//      return "withNbt";
//    }
//
//    @Override
//    public void invoke(ExecutionContext context, Object arg1, Object arg2)
//        throws ResolvedControlThrowable {
//      WolBlock self = getConverters().toJava(WolBlock.class, arg1, 1, "self", getName());
//      Table nbt = getConverters().toJava(Table.class, arg2, 2, "nbt", getName());
//
//      NBTTagCompound oldNbt = self.getNbt();
//      NBTTagCompound newNbt;
//      if (oldNbt != null) {
//        newNbt = getClassLoader().getConverters().getNbtConverter().merge(oldNbt, nbt);
//      } else {
//        // newNbt = oldNbt;
//        throw new IllegalArgumentException(String.format("Can't set nbt for block '%s'",
//            self.getBlockState().getBlock().getRegistryName().getResourcePath()));
//      }
//
//      ImmutableWolBlock newWolBlock = new ImmutableWolBlock(self.getBlockState(), newNbt);
//      Object result = getConverters().toLua(newWolBlock);
//      context.getReturnBuffer().setTo(result);
//    }
//  }
//
//  private class AsItemFunction extends NamedFunction2 {
//    @Override
//    public String getName() {
//      return "asItem";
//    }
//
//    @Override
//    public void invoke(ExecutionContext context, Object arg1, Object arg2)
//        throws ResolvedControlThrowable {
//      WolBlock self = getConverters().toJava(WolBlock.class, arg1, 1, "self", getName());
//      int amount =
//          getConverters().toJavaOptional(Integer.class, arg2, 2, "amount", getName()).orElse(1);
//      ItemStack itemStack = self.asItemStack(amount);
//      Object result = getConverters().toLua(itemStack);
//      context.getReturnBuffer().setTo(result);
//    }
//  }
//}
