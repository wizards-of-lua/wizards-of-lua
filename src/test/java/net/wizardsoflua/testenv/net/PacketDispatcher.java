package net.wizardsoflua.testenv.net;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketDispatcher {

  /**
   * The SimpleNetworkWrapper instance is used both to register and send packets. Since I will be
   * adding wrapper methods, this field is private, but you should make it public if you plan on
   * using it directly.
   */
  private final SimpleChannel dispatcher;
  private final PacketDispatcherContext packetDispatcherContext;
  private byte packetIdCount = 0;

  public PacketDispatcher(String channelName, PacketDispatcherContext packetDispatcherContext) {
    this.packetDispatcherContext = packetDispatcherContext;
    ResourceLocation name = new ResourceLocation(channelName);
    Supplier<String> networkProtocolVersion = () -> "";
    Predicate<String> clientAcceptedVersions = it -> true;
    Predicate<String> serverAcceptedVersions = it -> true;
    dispatcher = NetworkRegistry.newSimpleChannel(name, networkProtocolVersion,
        clientAcceptedVersions, serverAcceptedVersions);
  }

  /**
   * Registers an {@link AbstractMessage} to the appropriate side(s)
   */
  public final <T extends AbstractMessage> void registerMessage(Class<T> clazz) {
    int packetId = packetIdCount++;
    boolean requiresMainThread = requiresMainThread(clazz);
    BiConsumer<T, PacketBuffer> encoder = AbstractMessage::write;
    Function<PacketBuffer, T> decoder = buffer -> {
      try {
        T message = clazz.newInstance();
        message.read(buffer);
        return message;
      } catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    };
    if (ClientHandledMessage.class.isAssignableFrom(clazz)) {
      BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer = (message,
          ctx) -> onMessage((ClientHandledMessage) message, ctx.get(), requiresMainThread);
      dispatcher.registerMessage(packetId, clazz, encoder, decoder, messageConsumer);
    }
    if (ServerHandledMessage.class.isAssignableFrom(clazz)) {
      BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer = (message,
          ctx) -> onMessage((ServerHandledMessage) message, ctx.get(), requiresMainThread);
      dispatcher.registerMessage(packetId, clazz, encoder, decoder, messageConsumer);
    }
  }

  private void onMessage(ClientHandledMessage message, NetworkEvent.Context ctx,
      boolean requiresMainThread) {
    execute(requiresMainThread, ctx, new Runnable() {
      @Override
      public void run() {
        message.handleClientSide(packetDispatcherContext.getPlayerEntity(ctx));
      }
    });
  }

  private void onMessage(ServerHandledMessage message, NetworkEvent.Context ctx,
      boolean requiresMainThread) {
    execute(requiresMainThread, ctx, new Runnable() {
      @Override
      public void run() {
        message.handleServerSide(packetDispatcherContext.getPlayerEntity(ctx));
      }
    });
  }

  private void execute(boolean runInMainThread, NetworkEvent.Context ctx, Runnable r) {
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

  public void sendTo(AbstractMessage message, EntityPlayerMP player) {
    dispatcher.send(player, message);
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
