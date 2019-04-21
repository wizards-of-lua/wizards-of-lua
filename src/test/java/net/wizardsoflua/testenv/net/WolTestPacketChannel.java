package net.wizardsoflua.testenv.net;

import java.util.ServiceConfigurationError;
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

public class WolTestPacketChannel {
  private final ResourceLocation name = new ResourceLocation(WizardsOfLua.MODID, "channel/test");
  private final SimpleChannel channel =
      NetworkRegistry.newSimpleChannel(name, this::getNetworkProtocolVersion,
          this::clientAcceptsServerVersion, this::serverAcceptsClientVersion);

  private String getNetworkProtocolVersion() {
    return "1";
  }

  private boolean clientAcceptsServerVersion(String serverVersion) {
    return true;
  }

  private boolean serverAcceptsClientVersion(String serverVersion) {
    return true;
  }

  public WolTestPacketChannel() {
    int index = 0;
    registerNetworkMessage(index++, ChatMessage.class, ChatMessage::new);
    registerNetworkMessage(index++, ClientChatReceivedMessage.class,
        ClientChatReceivedMessage::new);
    registerNetworkMessage(index++, LeftClickMessage.class, LeftClickMessage::new);
    registerNetworkMessage(index++, ReconnectMessage.class, ReconnectMessage::new);
    registerNetworkMessage(index++, RespawnMessage.class, RespawnMessage::new);
    registerNetworkMessage(index++, RightClickMessage.class, RightClickMessage::new);

    // FIXME Adrodoc 22.04.2019: Fix SPI for NetworkMessage
    // Set<Class<? extends NetworkMessage>> messageTypes =
    // ServiceLoader.load(NetworkMessage.class, NetworkMessage.class.getClassLoader());
    // for (Class<? extends NetworkMessage> messageType : messageTypes) {
    // registerNetworkMessage(index++, messageType);
    // }
  }

  private <M extends NetworkMessage> void registerNetworkMessage(int index, Class<M> messageType) {
    registerNetworkMessage(index, messageType, () -> {
      try {
        return messageType.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        throw new ServiceConfigurationError(NetworkMessage.class.getName() + ": " + "Provider "
            + messageType.getName() + " could not be instantiated", e);
      }
    });
  }

  private <M extends NetworkMessage> void registerNetworkMessage(int index, Class<M> messageType,
      Supplier<? extends M> constructor) {
    BiConsumer<M, PacketBuffer> encoder = NetworkMessage::encode;
    Function<PacketBuffer, M> decoder = buffer -> NetworkMessage.decode(constructor, buffer);
    BiConsumer<M, Supplier<Context>> messageConsumer = NetworkMessage::handle;
    channel.registerMessage(index, messageType, encoder, decoder, messageConsumer);
  }

  public void sendTo(EntityPlayerMP player, NetworkMessage message) {
    channel.sendTo(message, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
  }

  public void sendToServer(NetworkMessage message) {
    channel.sendToServer(message);
  }
}
