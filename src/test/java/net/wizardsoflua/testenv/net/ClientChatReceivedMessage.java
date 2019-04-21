package net.wizardsoflua.testenv.net;

import java.util.function.Supplier;
import com.google.auto.service.AutoService;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.wizardsoflua.testenv.event.TestPlayerReceivedChatEvent;

@AutoService(NetworkMessage.class)
public class ClientChatReceivedMessage implements NetworkMessage {
  private String text;

  public ClientChatReceivedMessage(String text) {
    this.text = text;
  }

  public ClientChatReceivedMessage() {}

  @Override
  public void decode(PacketBuffer buffer) {
    text = buffer.readString(MAX_STRING_LENGTH);
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeString(text, MAX_STRING_LENGTH);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
    Context context = contextSupplier.get();
    EntityPlayerMP player = context.getSender();
    MinecraftForge.EVENT_BUS.post(new TestPlayerReceivedChatEvent(player, text));
  }
}
