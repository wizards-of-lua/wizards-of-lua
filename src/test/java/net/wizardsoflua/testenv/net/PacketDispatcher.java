package net.wizardsoflua.testenv.net;

import java.util.Arrays;
import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketDispatcher {

  /**
   * The SimpleNetworkWrapper instance is used both to register and send packets. Since I will be
   * adding wrapper methods, this field is private, but you should make it public if you plan on
   * using it directly.
   */
  private final SimpleNetworkWrapper dispatcher;
  private final PacketDispatcherContext packetDispatcherContext;
  private byte packetIdCount = 0;

  public PacketDispatcher(String channelName, PacketDispatcherContext packetDispatcherContext) {
    this.packetDispatcherContext = packetDispatcherContext;
    dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
  }

  /**
   * Registers an {@link AbstractMessage} to the appropriate side(s)
   */
  public final <T extends AbstractMessage> void registerMessage(Class<T> clazz) {
    final EnumSet<Side> sides = getMessageHandlingSides(clazz);
    IMessageHandler<AbstractMessage, AbstractMessage> handler =
        new IMessageHandler<AbstractMessage, AbstractMessage>() {
          @Override
          public AbstractMessage onMessage(AbstractMessage message, MessageContext ctx) {
            if (sides.contains(ctx.side)) {
              PacketDispatcher.this.onMessage(message, ctx);
              return null;
            } else {
              throw new RuntimeException(
                  "Invalid side " + ctx.side.name() + " for " + getClass().getSimpleName());
            }
          }
        };
    int packetId = packetIdCount++;
    for (Side side : sides) {
      dispatcher.registerMessage(handler, clazz, packetId, side);
    }
  }

  private void onMessage(AbstractMessage message, MessageContext ctx) {
    if (message.requiresMainThread()) {
      checkThreadAndEnqueue(message, ctx);
    } else {
      message.process(packetDispatcherContext.getPlayerEntity(ctx), ctx.side);
    }
  }

  private EnumSet<Side> getMessageHandlingSides(Class<?> clazz) {
    Class<?> current = clazz;
    while (current != null) {
      MessageHandling a = current.getAnnotation(MessageHandling.class);
      if (a != null) {
        return EnumSet.copyOf(Arrays.asList(a.value()));
      }
      current = current.getSuperclass();
    }
    throw new IllegalArgumentException("Missing " + MessageHandling.class.getSimpleName()
        + " annotation on class " + clazz.getName());
  }

  private final void checkThreadAndEnqueue(final AbstractMessage msg, final MessageContext ctx) {
    IThreadListener thread = packetDispatcherContext.getThreadFromContext(ctx);
    // pretty much copied straight from vanilla code, see {@link
    // PacketThreadUtil#checkThreadAndEnqueue}
    thread.addScheduledTask(new Runnable() {
      public void run() {
        msg.process(packetDispatcherContext.getPlayerEntity(ctx), ctx.side);
      }
    });
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
