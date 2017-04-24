package net.karneim.luamod.lua.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class ClickWindowEvent extends PlayerEvent {
  private final ItemStack clickedItem;
  private final ClickType clickType;
  private final int slotId;

  public ClickWindowEvent(EntityPlayer player, ItemStack clickedItem, ClickType clickType,
      int slotId) {
    super(player);
    this.clickedItem = clickedItem;
    this.clickType = clickType;
    this.slotId = slotId;
  }

  public ClickWindowEvent(EntityPlayer player, CPacketClickWindow clickWindow) {
    this(player, clickWindow.getClickedItem(), clickWindow.getClickType(), clickWindow.getSlotId());
  }

  public ItemStack getClickedItem() {
    return clickedItem;
  }

  public ClickType getClickType() {
    return clickType;
  }

  public int getSlotId() {
    return slotId;
  }
}
