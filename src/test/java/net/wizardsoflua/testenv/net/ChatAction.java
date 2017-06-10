package net.wizardsoflua.testenv.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class ChatAction extends ClientAction {

  public String text;

  public ChatAction() {}

  public ChatAction(String text) {
    this.text = text;
  }

  @Override
  public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
    writeString(buffer, text);
  }

  @Override
  public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
    text = readString(buffer);
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    System.out.println("text: " + text);
    Minecraft.getMinecraft().player.sendChatMessage(text);
  }

}
