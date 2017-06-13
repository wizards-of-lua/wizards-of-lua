package net.wizardsoflua.lua.runtime;

import net.minecraft.world.World;

public class Runtime {
  private final World world;
  private final int luaTicksLimit;
  private final int sleepTrigger;
  private int allowance;
  private int luaTotalTicks = 0;

  // Time measured in Game-Ticks:
  private long spellCreatedGameTime;
  private long nextSpellWakeUpGameTime;

  private boolean autoSleep = true;

  public Runtime(World world, int luaTicksLimit) {
    this.world = world;
    this.luaTicksLimit = luaTicksLimit;
    this.sleepTrigger = luaTicksLimit / 2;
    this.allowance = luaTicksLimit;
    this.spellCreatedGameTime = world.getTotalWorldTime();
  }
  
  public boolean isAutoSleep() {
    return autoSleep;
  }
  
  public void setAutoSleep(boolean isAutoSleep) {
    this.autoSleep = isAutoSleep;
  }
  
  public void resetAllowance() {
    this.allowance = luaTicksLimit;
  }

  public long getSpellLifetime() {
    return world.getTotalWorldTime() - spellCreatedGameTime;
  }

  public long getGameDayTime() {
    return world.getWorldTime();
  }

  public long getGameTotalTime() {
    return world.getTotalWorldTime();
  }

  public void startSleep(long duration) {
    if (duration <= 0 && allowance < sleepTrigger) {
      duration = 1;
    }
    this.nextSpellWakeUpGameTime = world.getTotalWorldTime() + duration;
  }

  public boolean isSleeping() {
    return nextSpellWakeUpGameTime > world.getTotalWorldTime();
  }

  public void consumeLuaTicks(long ticks) {
    luaTotalTicks += ticks;
    allowance -= ticks;
  }
  
  public long getLuaTicks() {
    return luaTotalTicks;
  }

  public long getAllowance() {
    return allowance;
  }

  public boolean shouldPause() {
    if ( isSleeping()) {
      return true;
    }
    if (autoSleep) {
      return allowance <= 0;
    }
    if (allowance < 0) {
      throw new IllegalStateException(
          "Spell has been broken automatically since it has exceeded its tick allowance!");
    }
    return false;
  }

}
