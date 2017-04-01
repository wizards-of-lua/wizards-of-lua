package net.karneim.luamod.lua.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;

public class AnimationHandEvent {

  private final CPacketAnimation msg;
  private final EntityPlayer player;

  public AnimationHandEvent(CPacketAnimation msg, EntityPlayer player) {
    this.msg = msg;
    this.player = player;
  }

  public EntityPlayer getPlayer() {
    return player;
  }

  public EnumHand getHand() {
    return msg.getHand();
  }
}
