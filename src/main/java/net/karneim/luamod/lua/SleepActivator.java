package net.karneim.luamod.lua;

import java.util.Collection;

import net.karneim.luamod.lua.event.EventQueue;

// TODO (mka) Find a better name
public interface SleepActivator {
  void startSleep(long ticks);

  boolean addEventQueue(EventQueue queue);

  boolean removeEventQueue(EventQueue queue);

  void waitForEvent(EventQueue queue, int ticks);

  void waitForEvents(Collection<? extends EventQueue> queue, int ticks);

  void stopWaitingForEvent();
}
