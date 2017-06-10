package net.wizardsoflua.testenv.net;

import net.minecraft.entity.player.EntityPlayer;

public abstract class ClientAction extends AbstractPacket {

  @Override
  public final void handleServerSide(EntityPlayer player) {
    // Nothing to do. We don't send this message to the server
  }

}
