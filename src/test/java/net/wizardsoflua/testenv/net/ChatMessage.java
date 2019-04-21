package net.wizardsoflua.testenv.net;

import static java.util.Objects.requireNonNull;
import java.util.function.Supplier;
import com.google.auto.service.AutoService;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

@AutoService(NetworkMessage.class)
public class ChatMessage implements NetworkMessage {
  public String text;

  public ChatMessage(String text) {
    this.text = requireNonNull(text, "text");
  }

  public ChatMessage() {}

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
    Minecraft.getInstance().player.sendChatMessage(text);
  }
}
