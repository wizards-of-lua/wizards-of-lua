package net.karneim.luamod.lua.event;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraftforge.fml.common.eventhandler.Event;

public class WhisperEvent extends Event {
  private final String sender;
  private final String message;

  public WhisperEvent(String sender, String message) {
    this.sender = checkNotNull(sender, "sender == null!");
    this.message = checkNotNull(message, "message == null!");
  }

  public String getSender() {
    return sender;
  }

  public String getMessage() {
    return message;
  }
}
