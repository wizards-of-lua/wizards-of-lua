package net.wizardsoflua.event;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
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
      EnumHand hand = animation.getHand();
      ItemStack item = player.getHeldItem(hand);
      super.channelRead(ctx, new WolCPacketAnimation(hand, new SwingArmEvent(player, hand, item)));
    } else {
      super.channelRead(ctx, msg);
    }
  }

  /**
   * We are "extending" this packet in order to be able to post our {@link SwingArmEvent} right
   * after the {@link CPacketAnimation} has been processed.
   */
  private static class WolCPacketAnimation extends CPacketAnimation {
    private final SwingArmEvent swingArmEvent;

    WolCPacketAnimation(EnumHand hand, SwingArmEvent swingArmEvent) {
      super(hand);
      this.swingArmEvent = swingArmEvent;
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
      // The statement below throws a ThreadQuickExitException
      // when it is called from outside the main loop.
      // This happens frequently, but don't bother since
      // this method will be called again later from the main loop.
      super.processPacket(handler);
      // The following statement will be called only if the above statement
      // "survives" the main loop check. That happens always at the
      // second call.
      MinecraftForge.EVENT_BUS.post(swingArmEvent);
    }

  }
}
