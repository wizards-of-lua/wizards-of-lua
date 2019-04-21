package net.wizardsoflua.testenv.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;

@RequiresMainThread
public abstract class AbstractMessage {
  protected abstract void read(PacketBuffer buffer);

  protected abstract void write(PacketBuffer buffer);

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

  public void fromBytes(ByteBuf buffer) {
    read(new PacketBuffer(buffer));
  }

  public void toBytes(ByteBuf buffer) {
    write(new PacketBuffer(buffer));
  }
}
