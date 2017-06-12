package net.wizardsoflua.testenv.net;

import net.minecraft.entity.player.EntityPlayer;

public interface ClientHandledMessage {

  public abstract void handleClientSide(EntityPlayer player);

}
