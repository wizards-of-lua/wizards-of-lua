package net.wizardsoflua.testenv.net;

import java.io.IOException;

import com.google.common.base.Throwables;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@RequiresMainThread
public abstract class AbstractMessage implements IMessage {
  protected abstract void read(PacketBuffer buffer) throws IOException;

  protected abstract void write(PacketBuffer buffer) throws IOException;

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

}
