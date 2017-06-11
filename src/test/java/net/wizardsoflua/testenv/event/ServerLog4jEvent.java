package net.wizardsoflua.testenv.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ServerLog4jEvent extends Event {

  private final String message;

  public ServerLog4jEvent(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

}
