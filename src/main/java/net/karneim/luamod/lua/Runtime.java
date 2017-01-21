package net.karneim.luamod.lua;

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
    if (duration <= 0 && ticks.getAllowance() < 0) {
      duration = 1;
    }
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
