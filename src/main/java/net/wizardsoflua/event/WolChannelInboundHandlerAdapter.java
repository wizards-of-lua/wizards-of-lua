package net.wizardsoflua.event;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraftforge.common.MinecraftForge;

public class WolChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {

  private EntityPlayerMP player;

  public WolChannelInboundHandlerAdapter(EntityPlayerMP player) {
    this.player = player;
  }

  public void setPlayer(EntityPlayerMP player) {
    this.player = player;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof CPacketAnimation) {
      CPacketAnimation animation = (CPacketAnimation) msg;
      ItemStack item = player.getHeldItem(animation.getHand());
      MinecraftForge.EVENT_BUS.post(new SwingArmEvent(player, animation.getHand(), item));
    }
    super.channelRead(ctx, msg);
  }
}
