package net.karneim.luamod.lua.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class AnimationHandEvent {
  private final EntityPlayer player;
  private final EnumHand hand;

  public AnimationHandEvent(EntityPlayer player, EnumHand hand) {
    this.player = player;
    this.hand = hand;
  }

  public EntityPlayer getPlayer() {
    return player;
  }

  public EnumHand getHand() {
    return hand;
  }
}
