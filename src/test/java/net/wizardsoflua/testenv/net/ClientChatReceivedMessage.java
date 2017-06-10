package net.wizardsoflua.testenv.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

public class ClientChatReceivedMessage extends AbstractPacket {

  public String message;

  public ClientChatReceivedMessage() {
  }
  
  public ClientChatReceivedMessage(String message) {
    this.message = message;
  }

  @Override
  public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
    writeString(buffer, message);
  }

  @Override
  public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
    message = readString(buffer);
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    // Nothing to do. We don't send this message to the client
  }

  @Override
  public void handleServerSide(EntityPlayer player) {
    EntityPlayerMP mpPlayer = (EntityPlayerMP)player;
    MinecraftForge.EVENT_BUS.post(new TestPlayerReceivedChatEvent(mpPlayer, message));
  }

}
