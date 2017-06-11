package net.wizardsoflua.testenv.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.wizardsoflua.testenv.client.ChatAction;
import net.wizardsoflua.testenv.client.ClickAction;
import net.wizardsoflua.testenv.client.ConfigMessage;
import net.wizardsoflua.testenv.server.ClientChatReceivedMessage;

public class PacketDispatcher {
  // a simple counter will allow us to get rid of 'magic' numbers used during packet registration
  private static byte packetId = 0;

  /**
   * The SimpleNetworkWrapper instance is used both to register and send packets. Since I will be
   * adding wrapper methods, this field is private, but you should make it public if you plan on
   * using it directly.
   */
  private final SimpleNetworkWrapper dispatcher;

  public PacketDispatcher(String channelNam) {
    dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(channelNam);
  }

  /**
   * Call this during pre-init or loading and register all of your packets (messages) here
   */
  public void registerPackets() {
    // Packets handled on CLIENT
    registerMessage(ChatAction.class);
    registerMessage(ClickAction.class);
    registerMessage(ConfigMessage.class);

    // Packets handled on SERVER
    registerMessage(ClientChatReceivedMessage.class);

    // Bidirectional packets:
    // - none so far -
  }
  
  public <T extends AbstractMessage<T> & IMessageHandler<T, IMessage>> void registerPackets(Iterable<Class<T>> packetClasses) {
    for (Class<T> class1 : packetClasses) {
      registerMessage(class1);
    }
  }

  /**
   * Registers an {@link AbstractMessage} to the appropriate side(s)
   */
  private final <T extends AbstractMessage<T> & IMessageHandler<T, IMessage>> void registerMessage(
      Class<T> clazz) {
    if (AbstractClientMessage.class.isAssignableFrom(clazz)) {
      dispatcher.registerMessage(clazz, clazz, packetId++, Side.CLIENT);
    } else if (AbstractServerMessage.class.isAssignableFrom(clazz)) {
      dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
    } else {
      dispatcher.registerMessage(clazz, clazz, packetId, Side.CLIENT);
      dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
    }
  }

  public void sendTo(IMessage message, EntityPlayerMP player) {
    dispatcher.sendTo(message, player);
  }

  /**
   * Send this message to everyone. See {@link SimpleNetworkWrapper#sendToAll(IMessage)}
   */
  public void sendToAll(IMessage message) {
    dispatcher.sendToAll(message);
  }

  /**
   * Send this message to everyone within a certain range of a point. See
   * {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
   */
  public void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
    dispatcher.sendToAllAround(message, point);
  }

  /**
   * Sends a message to everyone within a certain range of the coordinates in the same dimension.
   * Shortcut to {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
   */
  public void sendToAllAround(IMessage message, int dimension, double x, double y, double z,
      double range) {
    sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
  }

  /**
   * Sends a message to everyone within a certain range of the player provided. Shortcut to
   * {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
   */
  public void sendToAllAround(IMessage message, EntityPlayer player, double range) {
    sendToAllAround(message, player.getEntityWorld().provider.getDimension(), player.posX,
        player.posY, player.posZ, range);
  }

  /**
   * Send this message to everyone within the supplied dimension. See
   * {@link SimpleNetworkWrapper#sendToDimension(IMessage, int)}
   */
  public void sendToDimension(IMessage message, int dimensionId) {
    dispatcher.sendToDimension(message, dimensionId);
  }

  /**
   * Send this message to the server. See {@link SimpleNetworkWrapper#sendToServer(IMessage)}
   */
  public void sendToServer(IMessage message) {
    dispatcher.sendToServer(message);
  }
}
