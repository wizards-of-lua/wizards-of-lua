package net.wizardsoflua.testenv.net;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public class RespawnAction extends ClientAction {

  public RespawnAction() {}

  @Override
  protected void read(PacketBuffer buffer) throws IOException {}

  @Override
  protected void write(PacketBuffer buffer) throws IOException {}

  @Override
  public void handleClientSide(EntityPlayer player) {
    System.out.println("respawn");
    Minecraft.getMinecraft().player.respawnPlayer();
  }

}
