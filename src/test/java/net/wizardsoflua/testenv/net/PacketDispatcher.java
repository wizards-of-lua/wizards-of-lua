package net.wizardsoflua.testenv.net;

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
    int packetId = packetIdCount++;
    boolean requiresMainThread = requiresMainThread(clazz);
    if (ClientHandledMessage.class.isAssignableFrom(clazz)) {
      IMessageHandler<AbstractMessage, AbstractMessage> handler =
          new IMessageHandler<AbstractMessage, AbstractMessage>() {
            @Override
            public AbstractMessage onMessage(AbstractMessage message, MessageContext ctx) {
              PacketDispatcher.this.onMessage((ClientHandledMessage) message, ctx,
                  requiresMainThread);
              return null;
            }
          };
      dispatcher.registerMessage(handler, clazz, packetId, Side.CLIENT);
    }
    if (ServerHandledMessage.class.isAssignableFrom(clazz)) {
      IMessageHandler<AbstractMessage, AbstractMessage> handler =
          new IMessageHandler<AbstractMessage, AbstractMessage>() {
            @Override
            public AbstractMessage onMessage(AbstractMessage message, MessageContext ctx) {
              PacketDispatcher.this.onMessage((ServerHandledMessage) message, ctx,
                  requiresMainThread);
              return null;
            }
          };
      dispatcher.registerMessage(handler, clazz, packetId, Side.SERVER);
    }
  }

  private void onMessage(ClientHandledMessage message, MessageContext ctx,
      boolean requiresMainThread) {
    execute(requiresMainThread, ctx, new Runnable() {
      public void run() {
        message.handleClientSide(packetDispatcherContext.getPlayerEntity(ctx));
      }
    });
  }

  private void onMessage(ServerHandledMessage message, MessageContext ctx,
      boolean requiresMainThread) {
    execute(requiresMainThread, ctx, new Runnable() {
      public void run() {
        message.handleServerSide(packetDispatcherContext.getPlayerEntity(ctx));
      }
    });
  }

  private void execute(boolean runInMainThread, MessageContext ctx, Runnable r) {
    if (runInMainThread) {
      IThreadListener thread = packetDispatcherContext.getThreadFromContext(ctx);
      thread.addScheduledTask(r);
    } else {
      r.run();
    }
  }

  private boolean requiresMainThread(Class<?> clazz) {
    Class<?> current = clazz;
    while (current != null) {
      RequiresMainThread a = current.getAnnotation(RequiresMainThread.class);
      if (a != null) {
        return a.value();
      }
      current = current.getSuperclass();
    }
    return false;
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
