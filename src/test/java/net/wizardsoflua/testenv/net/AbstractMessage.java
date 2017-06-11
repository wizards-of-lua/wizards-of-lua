package net.wizardsoflua.testenv.net;

import java.io.IOException;

import com.google.common.base.Throwables;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.wizardsoflua.testenv.WolTestEnvironment;

public abstract class AbstractMessage<T extends AbstractMessage<T>>
    implements IMessage, IMessageHandler<T, IMessage> {
  protected abstract void read(PacketBuffer buffer) throws IOException;

  protected abstract void write(PacketBuffer buffer) throws IOException;

  public abstract void process(EntityPlayer player, Side side);

  protected boolean isValidOnSide(Side side) {
    return true; // default allows handling on both sides, i.e. a bidirectional packet
  }

  /**
   * Whether this message requires the main thread to be processed (i.e. it requires that the world,
   * player, and other objects are in a valid state).
   */
  protected boolean requiresMainThread() {
    return true;
  }

  protected void writeString(ByteBuf buffer, String string) {
    if (string == null) {
      buffer.writeInt(-1);
    } else {
      buffer.writeInt(string.length());
      buffer.writeBytes(string.getBytes());
    }
  }

  protected String readString(ByteBuf buffer) {
    int len = buffer.readInt();
    if (len == -1) {
      return null;
    } else {
      byte[] bytes = new byte[len];
      buffer.readBytes(bytes);
      return new String(bytes);
    }
  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    try {
      read(new PacketBuffer(buffer));
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public void toBytes(ByteBuf buffer) {
    try {
      write(new PacketBuffer(buffer));
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public final IMessage onMessage(T msg, MessageContext ctx) {
    if (!msg.isValidOnSide(ctx.side)) {
      throw new RuntimeException(
          "Invalid side " + ctx.side.name() + " for " + msg.getClass().getSimpleName());
    } else if (msg.requiresMainThread()) {
      checkThreadAndEnqueue(msg, ctx);
    } else {
      msg.process(WolTestEnvironment.proxy.getPlayerEntity(ctx), ctx.side);
    }
    return null;
  }

  private static final <T extends AbstractMessage<T>> void checkThreadAndEnqueue(
      final AbstractMessage<T> msg, final MessageContext ctx) {
    IThreadListener thread = WolTestEnvironment.proxy.getThreadFromContext(ctx);
    // pretty much copied straight from vanilla code, see {@link
    // PacketThreadUtil#checkThreadAndEnqueue}
    thread.addScheduledTask(new Runnable() {
      public void run() {
        msg.process(WolTestEnvironment.proxy.getPlayerEntity(ctx), ctx.side);
      }
    });
  }
}
