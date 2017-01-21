package net.karneim.luamod.lua;

import net.sandius.rembulan.util.Check;

public class Ticks {
  private long total = 0;
  private long max;
  private long allowance;

  public Ticks(long max) {
    this.max = max;
    this.allowance = Check.nonNegative(max);
  }

  public long getAllowance() {
    return allowance;
  }

  public void addTicks(long ticks) {
    total += ticks;
    allowance -= ticks;
  }

  public long getMax() {
    return max;
  }
  
  public void resetAllowance() {
    allowance = max;
  }
  
  public long getTotal() {
    return total;
  }
}
