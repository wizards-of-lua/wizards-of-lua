package net.wizardsoflua.testenv.client;

import net.wizardsoflua.testenv.net.AbstractClientMessage;

public abstract class ClientAction<S extends AbstractClientMessage<S>>
    extends AbstractClientMessage<S> {

  public ClientAction() {}

}
