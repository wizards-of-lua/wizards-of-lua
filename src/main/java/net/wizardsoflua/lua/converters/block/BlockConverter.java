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
import net.wizardsoflua.block.WolBlock;
import net.wizardsoflua.lua.converters.Converters;
import net.wizardsoflua.lua.converters.common.DelegatingProxy;
import net.wizardsoflua.lua.converters.nbt.NbtConverter;
import net.wizardsoflua.lua.converters.nbt.NbtPrimitiveConverter;
import net.wizardsoflua.lua.table.DefaultTableBuilder;

public class BlockConverter {
  public static final String METATABLE_NAME = "Block";

  private final Converters converters;
  private final Table metatable;

  public BlockConverter(Converters converters) {
    this.converters = converters;
    // TODO do declaration outside this class
    this.metatable = converters.getTypes().declare(METATABLE_NAME);
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
    Table properties = (Table) table.rawget("properties");
    if (properties != null) {
      for (IProperty<?> key : state.getPropertyKeys()) {
        Object luaValue = properties.rawget(key.getName());
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
      NBTTagCompound newData = NbtConverter.merge(origData, luaNbt);
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
      addImmutable("properties", getProperties(delegate.getBlockState()));
      if (delegate.getData() != null) {
        addImmutable("nbt", getNbt(delegate.getData()));
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

    public Table getProperties(IBlockState blockState) {
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



}
