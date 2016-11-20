package net.karneim.luamod.lua.event;

public enum EventType {
  WHISPER_EVENT, CHAT_EVENT;

  public boolean matches(Event event) {
    return this == event.getType();
  }
}
