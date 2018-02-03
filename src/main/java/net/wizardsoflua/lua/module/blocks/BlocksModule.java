package net.wizardsoflua.lua.module.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.block.ImmutableWolBlock;
import net.wizardsoflua.lua.Converters;

public class BlocksModule {
  public static BlocksModule installInto(Table env, Converters converters) {
    BlocksModule result = new BlocksModule(converters);
    env.rawset("Blocks", result.getLuaTable());
    return result;
  }

  private final Converters converters;
  private final Table luaTable = DefaultTable.factory().newTable();

  public BlocksModule(Converters converters) {
    this.converters = converters;
    luaTable.rawset("get", new Get());
  }

  private Block getBlockByName(String blockName) {
    ResourceLocation resourceLocation = new ResourceLocation(blockName);
    if (!Block.REGISTRY.containsKey(resourceLocation)) {
      throw new IllegalArgumentException(
          String.format("Can't find block with name '%s'", blockName));
    }
    Block block = (Block) Block.REGISTRY.getObject(resourceLocation);
    return block;
  }

  public Table getLuaTable() {
    return luaTable;
  }

  private class Get extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      String blockName = converters.toJava(String.class, arg1);
      Block block = getBlockByName(blockName);

      IBlockState blockState = block.getDefaultState();

      NBTTagCompound nbt = null;
      if (blockState.getBlock().hasTileEntity(blockState)) {
        World world = null; // This is safe in vanilla MC 1.11.2 as the world value is not used.
        // TODO alternatively pass the 'default' world
        TileEntity tileEntity = blockState.getBlock().createTileEntity(world, blockState);
        nbt = new NBTTagCompound();
        tileEntity.writeToNBT(nbt);
        patch(tileEntity, nbt);
      }
      ImmutableWolBlock wolBlock = new ImmutableWolBlock(blockState, nbt);

      Object result = converters.toLua(wolBlock);
      context.getReturnBuffer().setTo(result);
    }

    // This is a work around for the TileEntityShulkerBox, since it doesn't write the Items tag
    // if the items are empty.
    private void patch(TileEntity tileEntity, NBTTagCompound origData) {
      if (tileEntity instanceof TileEntityShulkerBox) {
        if (origData.getTag("Items") == null) {
          origData.setTag("Items", new NBTTagList());
        }
      }
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
}
