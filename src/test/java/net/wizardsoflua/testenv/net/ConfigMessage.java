package net.wizardsoflua.testenv.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class ConfigMessage extends AbstractPacket {

  public String wolVersionOnServer;
//  public boolean scenterIsEnabledOnServer;
//  public String targetsConfigContent;
//  public int detectionRadius;

  @Override
  public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
    writeString(buffer, wolVersionOnServer);
//    buffer.writeBoolean(scenterIsEnabledOnServer);
//    writeString(buffer, targetsConfigContent);
//    buffer.writeInt(detectionRadius);
  }

  @Override
  public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
    wolVersionOnServer = readString(buffer);
//    scenterIsEnabledOnServer = buffer.readBoolean();
//    targetsConfigContent = readString(buffer);
//    detectionRadius = buffer.readInt();
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    System.out.println("wolVersionOnServer: "+wolVersionOnServer);
    //ScenterMod.instance.getClientProxy().onMultiplayerServerMessage(this);
  }

  @Override
  public void handleServerSide(EntityPlayer player) {
    // TODO Auto-generated method stub
    // Nothing to do. We don't send this message to the server
  }

}
