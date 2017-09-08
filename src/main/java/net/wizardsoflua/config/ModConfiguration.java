package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModConfiguration {

  public static ModConfiguration create(FMLPreInitializationEvent event, String configName) {
    File configDir = new File(event.getModConfigurationDirectory(), configName);
    File cfgFile = new File(configDir, configName + ".cfg");
    if (event.getSuggestedConfigurationFile().exists() && !cfgFile.exists()) {
      event.getSuggestedConfigurationFile().renameTo(cfgFile);
    }

    Configuration config = new Configuration(cfgFile);
    config.load();
    return new ModConfiguration(config);
  }

  private IntProperty luaTicksLimit;
  private BooleanProperty shouldShowAboutMessage;

  public ModConfiguration(Configuration config) {
    checkNotNull(config, "config==null!");
    luaTicksLimit = new IntProperty(config, "general", "luaTicksLimit", 10000, 1000, 10000000,
        "Max. number of Lua ticks a spell can run per game tick");
    shouldShowAboutMessage = new BooleanProperty(config, "general", "showAboutMessage", true,
        "Shows the about message to the player at first login");
    if (config.hasChanged()) {
      config.save();
    }
  }

  public boolean shouldShowAboutMessage() {
    return shouldShowAboutMessage.getValue();
  }

  public int getLuaTicksLimit() {
    return luaTicksLimit.getValue();
  }

  public int setLuaTicksLimit(int value) {
    return luaTicksLimit.setValue(value, true);
  }

}
