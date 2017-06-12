package net.wizardsoflua.testenv.net;

import java.io.IOException;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public class ChatAction extends ClientAction {

  public String text;

  public ChatAction() {}

  public ChatAction(String text) {
    this(text, (Object[]) null);
  }

  public ChatAction(String format, Object... args) {
    if (args != null && args.length > 0) {
      this.text = String.format(format, args);
    } else {
      this.text = format;
    }
  }

  @Override
  protected void read(PacketBuffer buffer) throws IOException {
    text = readString(buffer);
  }

  @Override
  protected void write(PacketBuffer buffer) throws IOException {
    writeString(buffer, text);
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    System.out.println("text: " + text);
    ((EntityPlayerSP) player).sendChatMessage(text);
  }

}
