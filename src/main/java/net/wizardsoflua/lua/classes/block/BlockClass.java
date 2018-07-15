package net.wizardsoflua.lua.classes.block;

import java.util.Optional;
import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaFunctionDoc;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.block.ImmutableWolBlock;
import net.wizardsoflua.block.WolBlock;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.LuaInstance;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.classes.item.ItemClass;
import net.wizardsoflua.lua.extension.util.BasicLuaClass;
import net.wizardsoflua.lua.extension.util.LuaClassAttributes;
import net.wizardsoflua.lua.nbt.NbtConverter;

@AutoService(LuaConverter.class)
@LuaClassAttributes(name = BlockClass.NAME)
@GenerateLuaClassTable(instance = BlockClass.Instance.class)
@GenerateLuaDoc(subtitle = "All There is to Know About a Block")
public class BlockClass extends BasicLuaClass<WolBlock, BlockClass.Instance<?>> {
  public static final String NAME = "Block";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new BlockClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<?>> toLuaInstance(WolBlock javaInstance) {
    return new BlockClassInstanceTable<>(new Instance<>(javaInstance, injector), getTable(),
        converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends WolBlock> extends LuaInstance<D> {
    @Resource
    private NbtConverter nbtConverters;

    public Instance(D delegate, Injector injector) {
      super(delegate);
      injector.injectMembers(this);
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
    @LuaFunctionDoc(returnType = BlockClass.NAME, args = {"data"})
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
    @LuaFunctionDoc(returnType = BlockClass.NAME, args = {"nbt"})
    public WolBlock withNbt(Table nbt) {
      NBTTagCompound oldNbt = delegate.getNbt();
      NBTTagCompound newNbt;
      if (oldNbt != null) {
        newNbt = nbtConverters.merge(oldNbt, nbt);
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
    @LuaFunctionDoc(returnType = BlockClass.NAME, args = {})
    public WolBlock copy() {
      WolBlock self = delegate;
      ImmutableWolBlock newWolBlock = new ImmutableWolBlock(self.getBlockState(), self.getNbt());
      return newWolBlock;
    }
  }
}
