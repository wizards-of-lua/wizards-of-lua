package net.wizardsoflua.config;

import java.io.File;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModConfiguration {

  private final String configName;

  private File configDir;
  private ExtendedConfiguration config;

  private boolean shouldShowAboutMessage = true;
  private int luaTicksLimit = 10000;

  public ModConfiguration(String configName) {
    this.configName = configName;
  }

  public void init(FMLPreInitializationEvent event) {
    configDir = new File(event.getModConfigurationDirectory(), configName);
    File cfgFile = new File(this.configDir, configName + ".cfg");
    if (event.getSuggestedConfigurationFile().exists() && !cfgFile.exists()) {
      event.getSuggestedConfigurationFile().renameTo(cfgFile);
    }

    config = new ExtendedConfiguration(cfgFile);
    reload();
  }

  public void reload() {
    config.load();

    shouldShowAboutMessage = config.getBoolean("showAboutMessage", "general",
        shouldShowAboutMessage, "Shows the about message to the player at first login");
    luaTicksLimit = config.getInt("luaTicksLimit", "general", luaTicksLimit, 1000, 10000000,
        "Max. number of Lua ticks a spell can run per game tick");
    if (config.hasChanged()) {
      config.save();
    }
  }

  public void save() {
    config.setInt("luaTicksLimit", "general", luaTicksLimit);
    if (config.hasChanged()) {
      config.save();
    }
  }

  public boolean shouldShowAboutMessage() {
    return shouldShowAboutMessage;
  }

  public int getLuaTicksLimit() {
    return luaTicksLimit;
  }

  public void setLuaTicksLimit(int luaTicksLimit) {
    this.luaTicksLimit = luaTicksLimit;
    save();
  }
}
