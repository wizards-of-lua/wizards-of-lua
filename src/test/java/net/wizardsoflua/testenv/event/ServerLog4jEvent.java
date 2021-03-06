package net.wizardsoflua.testenv.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ServerLog4jEvent extends Event {

  private final String message;

  public ServerLog4jEvent(String message) {
    // Remove formatting codes. See net.minecraft.util.text.TextFormatting
    String formatPrefix = "\u00a7";
    String formatPattern = formatPrefix + ".";
    message = message.replaceAll(formatPattern, "");
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

}
