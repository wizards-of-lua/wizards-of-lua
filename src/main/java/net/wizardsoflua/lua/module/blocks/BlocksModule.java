package net.wizardsoflua.lua.module.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.block.WolBlock;
import net.wizardsoflua.lua.converters.Converters;
import net.wizardsoflua.lua.module.types.Terms;

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
          String.format("Can't find block with name '%'", blockName));
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
      String blockName = converters.getTypes().castString(arg1, Terms.MANDATORY);
      Block block = getBlockByName(blockName);

      IBlockState blockState = block.getDefaultState();
      NBTTagCompound nbt = null; // TODO
      WolBlock wolBlock = new WolBlock(blockState, nbt);

      Table result = converters.blockToLua(wolBlock);

      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
}
