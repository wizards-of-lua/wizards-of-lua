package net.wizardsoflua.testenv.net;

import net.minecraftforge.fml.relauncher.Side;

/**
 * Messages that can only be sent from the server to the client should use this class
 */
public abstract class AbstractClientMessage<T extends AbstractMessage<T>>
    extends AbstractMessage<T> {
  @Override
  protected final boolean isValidOnSide(Side side) {
    return side.isClient();
  }
}