package net.wizardsoflua.testenv.net;

import java.util.ServiceConfigurationError;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.extension.ServiceLoader;

public class WolTestPacketChannel {
  private static final ResourceLocation NAME =
      new ResourceLocation(WizardsOfLua.MODID, "channel/test");
  private static final SimpleChannel CHANNEL =
      NetworkRegistry.newSimpleChannel(NAME, () -> getNetworkProtocolVersion(),
          it -> clientAcceptsServerVersion(it), it -> serverAcceptsClientVersion(it));

  private static String getNetworkProtocolVersion() {
    return "1";
  }

  private static boolean clientAcceptsServerVersion(String serverVersion) {
    return true;
  }

  private static boolean serverAcceptsClientVersion(String clientVersion) {
    return true;
  }

  public static void initialize() {
    int index = 0;
    Set<Class<? extends NetworkMessage>> messageTypes =
        new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));
    messageTypes.addAll(ServiceLoader.load(NetworkMessage.class));
    for (Class<? extends NetworkMessage> messageType : messageTypes) {
      registerNetworkMessage(index++, messageType);
    }
  }

  private static <M extends NetworkMessage> void registerNetworkMessage(int index,
      Class<M> messageType) {
    registerNetworkMessage(index, messageType, () -> {
      try {
        return messageType.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        throw new ServiceConfigurationError(NetworkMessage.class.getName() + ": " + "Provider "
            + messageType.getName() + " could not be instantiated", e);
      }
    });
  }

  private static <M extends NetworkMessage> void registerNetworkMessage(int index,
      Class<M> messageType, Supplier<? extends M> constructor) {
    BiConsumer<M, PacketBuffer> encoder = NetworkMessage::encode;
    Function<PacketBuffer, M> decoder = buffer -> NetworkMessage.decode(constructor, buffer);
    BiConsumer<M, Supplier<Context>> messageConsumer = NetworkMessage::handle;
    CHANNEL.registerMessage(index, messageType, encoder, decoder, messageConsumer);
  }

  public static void sendTo(EntityPlayerMP player, NetworkMessage message) {
    CHANNEL.sendTo(message, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
  }

  public static void sendToServer(NetworkMessage message) {
    CHANNEL.sendToServer(message);
  }

  public static void reply(NetworkMessage reply, Context context) {
    CHANNEL.reply(reply, context);
  }
}
