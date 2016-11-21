package net.karneim.luamod.lua;

import java.util.Collection;

import net.karneim.luamod.lua.event.EventListener;

public interface SleepActivator {
  void startSleep(long ticks);

  boolean addEventListener(EventListener listener);

  boolean removeEventListener(EventListener listener);

  void waitForEvent(EventListener listener, int ticks);

  void waitForEvents(Collection<? extends EventListener> listener, int ticks);

  void stopWaitingForEvent();
}
