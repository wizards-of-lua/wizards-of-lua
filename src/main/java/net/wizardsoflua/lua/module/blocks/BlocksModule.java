package net.wizardsoflua.lua.module.blocks;

import com.google.auto.service.AutoService;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.block.ImmutableWolBlock;
import net.wizardsoflua.lua.extension.api.Converter;
import net.wizardsoflua.lua.extension.api.InitializationContext;
import net.wizardsoflua.lua.extension.api.function.NamedFunction1;
import net.wizardsoflua.lua.extension.spi.LuaExtension;
import net.wizardsoflua.lua.extension.util.AbstractLuaModule;

@AutoService(LuaExtension.class)
public class BlocksModule extends AbstractLuaModule {
  private Table table;
  private Converter converter;

  @Override
  public void initialize(InitializationContext context) {
    table = context.getTableFactory().newTable();
    converter = context.getConverter();
    add(new GetFunction());
  }

  @Override
  public String getName() {
    return "Blocks";
  }

  @Override
  public Table getLuaObject() {
    return table;
  }

  private ImmutableWolBlock get(String name) {
    Block block = getBlockByName(name);

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
    return new ImmutableWolBlock(blockState, nbt);
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

  // This is a work around for the TileEntityShulkerBox, since it doesn't write the Items tag
  // if the items are empty.
  private void patch(TileEntity tileEntity, NBTTagCompound origData) {
    if (tileEntity instanceof TileEntityShulkerBox) {
      if (origData.getTag("Items") == null) {
        origData.setTag("Items", new NBTTagList());
      }
    }
  }

  private class GetFunction extends NamedFunction1 {
    @Override
    public String getName() {
      return "get";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      String name = converter.toJava(String.class, arg1, 1, "name", getName());
      ImmutableWolBlock result = get(name);
      Object luaResult = converter.toLua(result);
      context.getReturnBuffer().setTo(luaResult);
    }
  }
}
