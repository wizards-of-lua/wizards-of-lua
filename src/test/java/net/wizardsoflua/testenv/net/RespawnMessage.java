package net.wizardsoflua.testenv.net;

import java.util.function.Supplier;
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
  public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
    Minecraft.getInstance().player.respawnPlayer();
  }
}
