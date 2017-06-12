package net.wizardsoflua.testenv.net;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.wizardsoflua.testenv.WolTestEnvironment;

public class PrepareForTestAction extends ClientAction {

  public int id;

  public PrepareForTestAction() {}

  public PrepareForTestAction(int id) {
    this.id = id;
  }

  @Override
  protected void read(PacketBuffer buffer) throws IOException {
    id = buffer.readInt();
  }

  @Override
  protected void write(PacketBuffer buffer) throws IOException {
    buffer.writeInt(id);
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    System.out.println("preparing for test: " + id);
    WolTestEnvironment.instance.getPacketDispatcher().sendToServer(new PreparedForTestMessage(id));
  }

}
