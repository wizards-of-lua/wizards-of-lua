package net.karneim.luamod.lua;

import net.minecraft.world.World;

public class Runtime {
  private final World world;
  private final LuaTicks luaticks;

  // Time measured in Game-Ticks:
  private long spellLifetime;
  private long nextSpellWakeUpTime;

  public Runtime(World world, LuaTicks luaticks) {
    this.world = world;
    this.luaticks = luaticks;
  }

  public void setSpellLifetime(long lifetime) {
    this.spellLifetime = lifetime;
  }

  public long getSpellLifetime() {
    return spellLifetime;
  }

  public long getGameDayTime() {
    return world.getWorldTime();
  }

  public long getGameTotalTime() {
    return world.getTotalWorldTime();
  }

  public void startSleep(long duration) {
    if (duration <= 0 && luaticks.getAllowance() < 0) {
      duration = 1;
    }
    this.nextSpellWakeUpTime = spellLifetime + duration;
  }

  public boolean isSleeping() {
    return nextSpellWakeUpTime > spellLifetime;
  }

  public long getLuaTicks() {
    return luaticks.getTotal();
  }

  public long getAllowance() {
    return luaticks.getAllowance();
  }

}
