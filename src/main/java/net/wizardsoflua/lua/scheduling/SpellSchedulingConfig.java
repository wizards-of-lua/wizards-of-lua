package net.wizardsoflua.lua.scheduling;

public class SpellSchedulingConfig {
  private final int luaTicksLimit;
  private final boolean autoSleep;

  public SpellSchedulingConfig(int luaTicksLimit, boolean autoSleep) {
    this.luaTicksLimit = luaTicksLimit;
    this.autoSleep = autoSleep;
  }

  public int getLuaTicksLimit() {
    return luaTicksLimit;
  }

  public boolean isAutoSleep() {
    return autoSleep;
  }

}
