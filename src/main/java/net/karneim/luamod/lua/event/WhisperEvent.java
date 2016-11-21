package net.karneim.luamod.lua.event;

public class WhisperEvent {
  public final String sender;
  public final String message;
  
  public WhisperEvent(String sender, String message) {
    this.sender = sender;
    this.message = message;
  }
}
