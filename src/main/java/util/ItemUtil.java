package util;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class ItemUtil {

  public static ItemStack getItemStackFromBlock(IBlockState blockState,
      @Nullable NBTTagCompound nbt) {
    Block block = blockState.getBlock();
    int meta = block.getMetaFromState(blockState);
    Item item = Item.getItemFromBlock(block);
    if (item == Items.AIR) {
      // Work-Around since Item.BLOCK_TO_ITEM is incomplete
      if (block == Blocks.STANDING_SIGN || block == Blocks.WALL_SIGN) {
        item = Item.REGISTRY.getObject(new ResourceLocation("sign"));
      }
    }
    if ( !item.getHasSubtypes()) {
      meta = 0;
    }
    ItemStack stack = new ItemStack(item, 1, meta);
    if (nbt == null) {
      return stack;
    } else {
      return storeTEInStack(stack, nbt);
    }
  }

  /**
   * Originally copied from {@link Minecraft#storeTEInStack(ItemStack, TileEntity)}
   * 
   */
  private static ItemStack storeTEInStack(ItemStack stack, NBTTagCompound nbttagcompound) {
    if (stack.getItem() == Items.SKULL && nbttagcompound.hasKey("Owner")) {
      NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("Owner");
      NBTTagCompound nbttagcompound3 = new NBTTagCompound();
      nbttagcompound3.setTag("SkullOwner", nbttagcompound2);
      stack.setTagCompound(nbttagcompound3);
      return stack;
    } else {
      stack.setTagInfo("BlockEntityTag", nbttagcompound);
      if (stack.isStackable()) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();
        nbttaglist.appendTag(new NBTTagString("(+NBT)"));
        nbttagcompound1.setTag("Lore", nbttaglist);
        stack.setTagInfo("display", nbttagcompound1);
      }
      return stack;
    }
  }
}
