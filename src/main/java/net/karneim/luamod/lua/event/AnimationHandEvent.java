package net.karneim.luamod.lua.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class AnimationHandEvent extends PlayerEvent {
  private final EnumHand hand;

  public AnimationHandEvent(EntityPlayer player, EnumHand hand) {
    super(player);
    this.hand = hand;
  }

  public EnumHand getHand() {
    return hand;
  }
}
