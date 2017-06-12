package net.wizardsoflua.testenv.net;

import net.minecraft.entity.player.EntityPlayer;

public interface ServerHandledMessage {

  public abstract void handleServerSide(EntityPlayer player);

}
