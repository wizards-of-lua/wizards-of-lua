package net.wizardsoflua.testenv.net;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public class ConfigMessage extends ClientAction {

  public String wolVersionOnServer;

  public ConfigMessage() {}

  public ConfigMessage(String wolVersionOnServer) {
    this.wolVersionOnServer = wolVersionOnServer;
  }

  @Override
  protected void read(PacketBuffer buffer) throws IOException {
    wolVersionOnServer = readString(buffer);
  }

  @Override
  protected void write(PacketBuffer buffer) throws IOException {
    writeString(buffer, wolVersionOnServer);
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    System.out.println("wolVersionOnServer: " + wolVersionOnServer);
  }

}
