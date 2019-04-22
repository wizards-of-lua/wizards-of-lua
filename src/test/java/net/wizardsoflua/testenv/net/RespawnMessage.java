package net.wizardsoflua.testenv.net;

import com.google.auto.service.AutoService;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

@AutoService(NetworkMessage.class)
public class RespawnMessage implements NetworkMessage {
  @Override
  public void decode(PacketBuffer buffer) {}

  @Override
  public void encode(PacketBuffer buffer) {}

  @Override
  public void handle(NetworkEvent.Context context) {
    Minecraft.getInstance().player.respawnPlayer();
  }
}
