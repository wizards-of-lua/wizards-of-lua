package net.wizardsoflua.testenv.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

@MessageHandling(Side.SERVER)
public class ClientChatReceivedMessage extends AbstractMessage {
  private String text;

  // The basic, no-argument constructor MUST be included to use the new automated handling
  public ClientChatReceivedMessage() {}

  // if there are any class fields, be sure to provide a constructor that allows
  // for them to be initialized, and use that constructor when sending the packet
  public ClientChatReceivedMessage(String text) {
    this.text = text;
  }

  @Override
  protected void read(PacketBuffer buffer) {
    text = readString(buffer);
  }

  @Override
  protected void write(PacketBuffer buffer) {
    writeString(buffer, text);
  }

  @Override
  public void process(EntityPlayer player, Side side) {

    EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
    MinecraftForge.EVENT_BUS.post(new TestPlayerReceivedChatEvent(mpPlayer, text));
  }

}
