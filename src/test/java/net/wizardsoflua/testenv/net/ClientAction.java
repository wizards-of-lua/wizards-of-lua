package net.wizardsoflua.testenv.net;

import net.minecraftforge.fml.relauncher.Side;

@MessageHandling(Side.CLIENT)
public abstract class ClientAction extends AbstractMessage {

  public ClientAction() {}

}
