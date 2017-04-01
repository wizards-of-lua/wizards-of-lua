package net.karneim.luamod.lua.event;

import net.minecraft.network.play.client.CPacketAnimation;

public class AnimationHandEvent {

  private final CPacketAnimation msg;

  public AnimationHandEvent(CPacketAnimation msg) {
    this.msg = msg;
  }

}
