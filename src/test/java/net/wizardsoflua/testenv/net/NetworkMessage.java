package net.wizardsoflua.testenv.net;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public interface NetworkMessage {
  int MAX_STRING_LENGTH = Short.MAX_VALUE;

  static <M extends NetworkMessage> M decode(Supplier<M> constructor, PacketBuffer buffer) {
    M result = constructor.get();
    result.decode(buffer);
    return result;
  }

  void decode(PacketBuffer buffer);

  void encode(PacketBuffer buffer);

  default void handle(Supplier<NetworkEvent.Context> contextSupplier) {
    Context context = contextSupplier.get();
    if (!context.getPacketHandled()) {
      handle(context);
      context.setPacketHandled(true);
    }
  }

  void handle(NetworkEvent.Context context);
}
