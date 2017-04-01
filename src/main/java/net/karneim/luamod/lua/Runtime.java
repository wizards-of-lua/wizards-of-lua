package net.karneim.luamod.lua;

public class Runtime {
  private final Ticks ticks;

  private long lifetime;
  private long wakeUpTime;

  public Runtime(Ticks ticks) {
    this.ticks = ticks;
  }

  public void setLifetime(long lifetime) {
    this.lifetime = lifetime;
  }

  public void startSleep(long duration) {
    if (duration <= 0 && ticks.getAllowance() < 0) {
      duration = 1;
    }
    this.wakeUpTime = lifetime + duration;
  }

  public boolean isSleeping() {
    return wakeUpTime > lifetime;
  }

  public long getLuaTicksTotal() {
    return ticks.getTotal();
  }

  public long getAllowance() {
    return ticks.getAllowance();
  }

  public long getLifetime() {
    return lifetime;
  }
  
  
}
