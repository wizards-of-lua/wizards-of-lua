package net.wizardsoflua.testenv.net;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public interface NetworkMessage {
  int MAX_STRING_LENGTH = Short.MAX_VALUE;

  static <M extends NetworkMessage> M decode(Supplier<M> constructor, PacketBuffer buffer) {
    M result = constructor.get();
    result.decode(buffer);
    return result;
  }

  void decode(PacketBuffer buffer);

  void encode(PacketBuffer buffer);

  void handle(Supplier<NetworkEvent.Context> contextSupplier);
}
