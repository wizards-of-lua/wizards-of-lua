package net.wizardsoflua.lua.module.time;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import net.minecraft.world.World;

public class Time {
  public interface Context {
    Clock getClock();
  }
  private final Context context;
  
  private final World world;
  private final int luaTicksLimit;
  private final int sleepTrigger;
  private int allowance;
  private long luaTotalTicks = 0;

  // Time measured in Game-Ticks:
  private long spellCreatedGameTime;
  private long nextSpellWakeUpGameTime;

  private boolean autoSleep = true;

  public Time(World world, int luaTicksLimit, Context context) {
    this.world = world;
    this.luaTicksLimit = luaTicksLimit;
    this.context = context;
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
  
  public String getDate(String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    if (pattern != null) {
      formatter = DateTimeFormatter.ofPattern(pattern);
    }
    String result = LocalDateTime.now(context.getClock()).format(formatter);
    return result;
  }

  public long getRealtime() {
    return context.getClock().millis();
  }

}
