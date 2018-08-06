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
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.block.ImmutableWolBlock;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.extension.LuaTableExtension;

@AutoService(SpellExtension.class)
@GenerateLuaModuleTable
@GenerateLuaDoc(name = BlocksModule.NAME, subtitle = "The Building Blocks Directory")
public class BlocksModule extends LuaTableExtension {
  public static final String NAME = "Blocks";
  @Resource
  private LuaConverters converters;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table getTable() {
    return new BlocksModuleTable<>(this, converters);
  }

  @LuaFunction
  public ImmutableWolBlock get(String name) {
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
    Block block = Block.REGISTRY.getObject(resourceLocation);
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
}
