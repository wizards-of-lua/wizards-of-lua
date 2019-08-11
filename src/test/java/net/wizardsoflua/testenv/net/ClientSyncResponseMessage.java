package net.wizardsoflua.testenv.net;

import com.google.auto.service.AutoService;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent;
import net.wizardsoflua.testenv.event.ClientSyncResponseEvent;

@AutoService(NetworkMessage.class)
public class ClientSyncResponseMessage implements NetworkMessage {
  @Override
  public void decode(PacketBuffer buffer) {}

  @Override
  public void encode(PacketBuffer buffer) {}

  @Override
  public void handle(NetworkEvent.Context context) {
    MinecraftForge.EVENT_BUS.post(new ClientSyncResponseEvent());
  }
}
