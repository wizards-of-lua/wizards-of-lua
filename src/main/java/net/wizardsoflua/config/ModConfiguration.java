package net.wizardsoflua.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModConfiguration {

  private final String configName;

  private File configDir;
  private Configuration config;

  private boolean shouldShowAboutMessage = true;

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
    config.load();

    shouldShowAboutMessage = config.getBoolean("showAboutMessage", "general",
        shouldShowAboutMessage, "Shows the about message to the player at first login");

    if (config.hasChanged()) {
      config.save();
    }
  }

  public boolean shouldShowAboutMessage() {
    return shouldShowAboutMessage;
  }
}
