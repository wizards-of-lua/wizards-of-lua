package net.wizardsoflua.testenv.net;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

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
  public void process(EntityPlayer player, Side side) {
    System.out.println("wolVersionOnServer: " + wolVersionOnServer);
  }

}
