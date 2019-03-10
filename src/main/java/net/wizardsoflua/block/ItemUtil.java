package net.wizardsoflua.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemUtil {

  public static ItemStack getItemStackFromBlock(IBlockState blockState,
      @Nullable NBTTagCompound nbt, int amount) {
    Block block = blockState.getBlock();

    // Item item = block.asItem();

    // TODO is there anything of the following to do here in 1.13?
    // if (item == Items.AIR) {
    // // Work-Around since Item.BLOCK_TO_ITEM is incomplete
    // if (block == Blocks.STANDING_SIGN || block == Blocks.WALL_SIGN) {
    // item = Item.REGISTRY.getObject(new ResourceLocation("sign"));
    // }
    // }
    // if (!item.getHasSubtypes()) {
    // meta = 0;
    // }
    //
    ItemStack stack = new ItemStack(block, amount);
    if (nbt == null) {
      return stack;
    } else {
      return storeTEInStack(stack, nbt);
    }
  }

  @Nullable
  public static EntityItem dropItem(World world, Vec3d pos, Item item, int amount) {
    return dropItem(world, pos, new ItemStack(item, amount));
  }

  /**
   * copied from net.minecraft.entity.Entity.entityDropItem(ItemStack, float)
   */
  @Nullable
  public static EntityItem dropItem(World world, Vec3d pos, ItemStack stack) {
    if (stack.isEmpty()) {
      return null;
    } else {
      EntityItem result = new EntityItem(world, pos.x, pos.y, pos.z, stack);
      result.setDefaultPickupDelay();
      world.spawnEntity(result);
      return result;
    }
  }

  /**
   * Originally copied from {@link Minecraft#storeTEInStack(ItemStack, TileEntity)}. Replaced 2nd
   * parameter {@link TileEntity} with {@link NBTTagCompound}.
   */
  private static ItemStack storeTEInStack(ItemStack stack, NBTTagCompound nbttagcompound) {
    if (stack.getItem() instanceof ItemSkull && nbttagcompound.hasKey("Owner")) {
      NBTTagCompound nbttagcompound2 = nbttagcompound.getCompound("Owner");
      stack.getOrCreateTag().setTag("SkullOwner", nbttagcompound2);
      return stack;
    } else {
      stack.setTagInfo("BlockEntityTag", nbttagcompound);
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      NBTTagList nbttaglist = new NBTTagList();
      nbttaglist.add(new NBTTagString("(+NBT)"));
      nbttagcompound1.setTag("Lore", nbttaglist);
      stack.setTagInfo("display", nbttagcompound1);
      return stack;
    }
  }

}
