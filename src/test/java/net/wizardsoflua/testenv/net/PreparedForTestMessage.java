package net.wizardsoflua.testenv.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.wizardsoflua.testenv.event.TestPlayerPreparedForTestEvent;

public class PreparedForTestMessage extends AbstractMessage implements ServerHandledMessage {
  private int id;

  public PreparedForTestMessage() {}

  public PreparedForTestMessage(int id) {
    this.id = id;
  }

  @Override
  protected void read(PacketBuffer buffer) {
    id = buffer.readInt();
  }

  @Override
  protected void write(PacketBuffer buffer) {
    buffer.writeInt(id);
  }

  @Override
  public void handleServerSide(EntityPlayer player) {
    EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
    MinecraftForge.EVENT_BUS.post(new TestPlayerPreparedForTestEvent(mpPlayer, id));
  }

}
