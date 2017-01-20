package net.karneim.luamod.lua;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.karneim.luamod.LuaMod;

public class Runtime {
  private final Ticks ticks;

  private long currentTime;
  private long wakeUpTime;

  public Runtime(Ticks ticks) {
    this.ticks = ticks;
  }

  public void setCurrentTime(long currentTime) {
    this.currentTime = currentTime;
  }

  public void startSleep(long duration) {
    this.wakeUpTime = currentTime + duration;
  }

  public boolean isSleeping() {
    return wakeUpTime > currentTime;
  }

  public long getLuaTicksTotal() {
    return ticks.getTotal();
  }

  public long getAllowance() {
    return ticks.getAllowance();
  }
}
