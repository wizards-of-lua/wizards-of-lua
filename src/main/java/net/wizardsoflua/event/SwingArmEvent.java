package net.wizardsoflua.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.eventbus.api.Event;

public class SwingArmEvent extends Event {
  private final EntityPlayer player;
  private final EnumHand hand;
  private final ItemStack itemStack;

  public SwingArmEvent(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
    this.player = player;
    this.hand = hand;
    this.itemStack = itemStack;
  }

  public EntityPlayer getPlayer() {
    return player;
  }

  public EnumHand getHand() {
    return hand;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

}
