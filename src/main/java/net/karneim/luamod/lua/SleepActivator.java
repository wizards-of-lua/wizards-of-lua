package net.karneim.luamod.lua;

import net.karneim.luamod.lua.event.EventListener;

public interface SleepActivator {
  void startSleep(long ticks);

  void addEventListener(EventListener listener);

  void removeEventListener(EventListener listener);

  void waitForEvent(EventListener listener, int ticks);

  void stopWaitingForEvent();
}
