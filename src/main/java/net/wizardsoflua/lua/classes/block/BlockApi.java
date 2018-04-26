package net.wizardsoflua.lua.classes.block;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaFunctionDoc;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.block.ImmutableWolBlock;
import net.wizardsoflua.block.WolBlock;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;
import net.wizardsoflua.lua.classes.LuaClassApi;
import net.wizardsoflua.lua.classes.item.ItemClass;

@GenerateLuaClass(name = BlockApi.NAME)
@GenerateLuaDoc(subtitle = "Bla")
public class BlockApi<D extends WolBlock> extends LuaClassApi<D> {
  public static final String NAME = "Block";

  public BlockApi(DelegatorLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }

  @LuaProperty
  public String getName() {
    ResourceLocation name = delegate.getBlockState().getBlock().getRegistryName();
    if ("minecraft".equals(name.getResourceDomain())) {
      return name.getResourcePath();
    } else {
      return name.toString();
    }
  }

  @LuaProperty(type = "Material")
  public Material getMaterial() {
    Material mat = delegate.getBlockState().getMaterial();
    return mat;
  }

  @LuaProperty(type = "table")
  public WolBlockState getData() {
    IBlockState blockState = delegate.getBlockState();
    return new WolBlockState(blockState);
  }

  @LuaProperty(type = "table")
  public @Nullable NBTTagCompound getNbt() {
    NBTTagCompound nbt = delegate.getNbt();
    if (nbt == null) {
      return null;
    }
    return nbt;
  }

  @LuaFunction
  @LuaFunctionDoc(returnType = BlockApi.NAME, args = {"data"})
  public WolBlock withData(Table data) {
    WolBlock self = delegate;
    IBlockState state = self.getBlockState();
    for (IProperty<?> key : state.getPropertyKeys()) {
      Object luaValue = data.rawget(key.getName());
      if (luaValue != null) {
        state = withProperty(state, key, luaValue);
      }
    }

    ImmutableWolBlock newWolBlock = new ImmutableWolBlock(state, self.getNbt());
    return newWolBlock;
  }

  private <T extends Comparable<T>> IBlockState withProperty(IBlockState state, IProperty<T> key,
      Object luaValue) {
    T javaValue = BlockPropertyConverter.toJava(key.getValueClass(), luaValue);
    return state.withProperty(key, javaValue);
  }

  @LuaFunction
  @LuaFunctionDoc(returnType = BlockApi.NAME, args = {"nbt"})
  public WolBlock withNbt(Table nbt) {
    NBTTagCompound oldNbt = delegate.getNbt();
    NBTTagCompound newNbt;
    if (oldNbt != null) {
      newNbt = getConverters().getNbtConverter().merge(oldNbt, nbt);
    } else {
      throw new IllegalArgumentException(String.format("Can't set nbt for block '%s'",
          delegate.getBlockState().getBlock().getRegistryName().getResourcePath()));
    }

    ImmutableWolBlock newWolBlock = new ImmutableWolBlock(delegate.getBlockState(), newNbt);
    return newWolBlock;
  }

  @LuaFunction
  @LuaFunctionDoc(returnType = ItemClass.NAME, args = {"amount"})
  public ItemStack asItem(@Nullable Integer amount) {
    ItemStack itemStack = delegate.asItemStack(Optional.ofNullable(amount).orElse(1));
    return itemStack;
  }

  @LuaFunction
  @LuaFunctionDoc(returnType = BlockApi.NAME, args = {})
  public WolBlock copy() {
    WolBlock self = delegate;
    ImmutableWolBlock newWolBlock = new ImmutableWolBlock(self.getBlockState(), self.getNbt());
    return newWolBlock;
  }
}
