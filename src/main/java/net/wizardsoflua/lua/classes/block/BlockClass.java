package net.wizardsoflua.lua.classes.block;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.inject.Inject;

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
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.LuaInstance;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.classes.item.ItemClass;
import net.wizardsoflua.lua.nbt.NbtConverter;

/**
 * The <span class="notranslate">Block</span> class is a basic unit of structure in Minecraft.
 *
 * An instance of this class represents either one of the following types:
 *
 * 1. A live block reference - that is a block at a specific world position. It can be accessed by
 * [spell.block](/modules/Spell/#block). 'Live' means that whenever the block at that position
 * changes, the internal state of this object will change as well.
 *
 * 2. An immutable block value - it's a block that exists independently of the world. It can be
 * created, e.g. by calling [Blocks.get()](/modules/Blocks/#get) or
 * [Block:copy()](/modules/Block/#copy).
 *
 * Both types are 'unmodifiable', meaning that you can't change their internal states directly.
 * Instead, if you want to change a block in the world, you will need to assign a new value to the
 * [spell.block](/modules/Spell/#block) field. This will copy the state of the right-hand value into
 * the block at the given spell's position.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = BlockClass.NAME)
@GenerateLuaClassTable(instance = BlockClass.Instance.class)
@GenerateLuaDoc(subtitle = "All There is to Know About a Block")
public final class BlockClass extends BasicLuaClass<WolBlock, BlockClass.Instance<WolBlock>> {
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
  protected Delegator<Instance<WolBlock>> toLuaInstance(WolBlock javaInstance) {
    return new BlockClassInstanceTable<>(new Instance<>(javaInstance, injector), getTable(),
        converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends WolBlock> extends LuaInstance<D> {
    @Inject
    private NbtConverter nbtConverters;

    public Instance(D delegate, Injector injector) {
      super(delegate);
      injector.injectMembers(this);
    }

    /**
     * The 'data' value is a table of block-specifc key-value pairs that provide human readable
     * information about the [block's data](https://minecraft.gamepedia.com/Data_values#Data). For
     * example, a grass block has a property called 'snowy' which can be true or false, and a
     * furnace has a property called 'facing' which can be one of 'north', 'east', 'south', and
     * 'west'.
     *
     */
    @LuaProperty
    public WolBlockState getData() {
      IBlockState blockState = delegate.getBlockState();
      return new WolBlockState(blockState);
    }

    /**
     * The 'material' give you some insights in how this block behaves. Please have a look into the
     * [Material Book](/modules/Material/) for more information.
     */
    @LuaProperty
    public Material getMaterial() {
      Material mat = delegate.getBlockState().getMaterial();
      return mat;
    }

    /**
     * This is the basic name of the block, e.g. 'grass', 'stone', or 'air'.
     */
    @LuaProperty
    public String getName() {
      ResourceLocation name = delegate.getBlockState().getBlock().getRegistryName();
      if ("minecraft".equals(name.getResourceDomain())) {
        return name.getResourcePath();
      } else {
        return name.toString();
      }
    }

    /**
     * The 'nbt' value (short for Named Binary Tag) is a table of block-specifc key-value pairs
     * about the [block's entity](https://minecraft.gamepedia.com/Block_entity_format). Only a small
     * amount of blocks do have a block entity. For example, the sign's entity contains information
     * about its text, and the chest's entity contains information about its content.
     */
    @LuaProperty
    public @Nullable NBTTagCompound getNbt() {
      NBTTagCompound nbt = delegate.getNbt();
      if (nbt == null) {
        return null;
      }
      return nbt;
    }

    /**
     * The 'withData' function returns a modified copy of the given block with the given table
     * values as the [block's data](https://minecraft.gamepedia.com/Data_values#Data).
     *
     * #### Example
     *
     * Creating a smooth diorite block and placing it at the spell's position.
     *
     * <code>
     * spell.block = Blocks.get( "stone"):withData(
     *   { variant = "smooth_diorite"}
     * )
     * </code>
     *
     * #### Example
     *
     * Creating a bundle of full grown wheat on top of the block at the spell's position.
     *
     * <code>
     * spell:move( "up")
     * spell.block = Blocks.get( "wheat"):withData(
     *   { age = 7}
     * )
     * </code>
     *
     */
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

    /**
     * The 'withNbt' function returns a modified copy of this block with the given table values for
     * the [block's entity](https://minecraft.gamepedia.com/Block_entity_format).
     *
     * #### Example
     *
     * Creating a standing sign with the name of the current spell's owner written onto it and
     * placing it at the spell's position.
     *
     * <code>
     * spell.block = Blocks.get( "standing_sign"):withNbt( {
     *   Text1 = '{"text":"'..spell.owner.name..'"}'
     * })
     * </code>
     *
     * #### Example
     *
     * Creating a wall sign showing the current time.
     *
     * <code>
     * spell:move("back")
     * spell.rotationYaw=spell.rotationYaw+180
     * spell.block=Blocks.get("wall_sign"):withData({facing=spell.facing})
     * while true do
     *   local time=Time.getDate("HH:mm:ss")
     *   spell.block=spell.block:withNbt({Text1= '{"text":"'..time..'"}'})
     *   sleep(20)
     * end
     * </code>
     *
     * #### Example
     *
     * Putting a stack of 64 wheat bundles into slot no. 5 of the chest (or the shulker box) at the
     * spell's position.
     *
     * <code>
     * spell.block = spell.block:withNbt( {
     *   Items = {
     *     { Count = 64, Damage = 0, Slot = 5,
     *       id = "minecraft:wheat"
     *     }
     *   }
     * })
     * </code>
     *
     */
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

    /**
     * The 'asItem' function returns this block as an [item](/modules/Item/) of the given amount.
     *
     * #### Example
     *
     * Creating an item from the block at the spell's current position and putting it into the
     * wizard's offhand.
     *
     * <code>
     * item=spell.block:asItem(); spell.owner.offhand=item
     * </code>
     *
     * Creating a full stack of of the block at the spell's current position and putting it into the
     * wizard's offhand.
     *
     * <code>
     * item=spell.block:asItem(64); spell.owner.offhand=item
     * </code>
     *
     */
    @LuaFunction
    @LuaFunctionDoc(returnType = ItemClass.NAME, args = {"amount"})
    public ItemStack asItem(@Nullable Integer amount) {
      ItemStack itemStack = delegate.asItemStack(Optional.ofNullable(amount).orElse(1));
      return itemStack;
    }

    /**
     * the 'copy' function returns a copy of this block.
     */
    @LuaFunction
    @LuaFunctionDoc(returnType = BlockClass.NAME, args = {})
    public WolBlock copy() {
      WolBlock self = delegate;
      ImmutableWolBlock newWolBlock = new ImmutableWolBlock(self.getBlockState(), self.getNbt());
      return newWolBlock;
    }
  }
}
