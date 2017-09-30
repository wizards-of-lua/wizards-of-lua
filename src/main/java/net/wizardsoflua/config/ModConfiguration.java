package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.ConfigCategory;
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

    if (config.hasKey("luatickslimit", "general")) {
      // Little fix because of bad config entry
      // See https://github.com/wizards-of-lua/wizards-of-lua/issues/51
      // TODO remove this when we left alpha phase
      config.removeCategory(config.getCategory("luatickslimit"));
    }
    return new ModConfiguration(configDir, config);
  }

  @SuppressWarnings("unused")
  private final File configDir;
  private Configuration config;
  private final IntProperty luaTicksLimit;
  private final BooleanProperty shouldShowAboutMessage;
  private final File luaHomeDir;
  private Map<String, UserConfig> userConfigs = new HashMap<>();

  public ModConfiguration(File configDir, Configuration config) {
    this.configDir = checkNotNull(configDir, "configDir==null!");
    this.config = checkNotNull(config, "config==null!");;
    luaHomeDir = new File(configDir, "home");

    luaTicksLimit = new IntProperty(config, "general", "luaTicksLimit", 10000, 1000, 10000000,
        "Max. number of Lua ticks a spell can run per game tick");
    shouldShowAboutMessage = new BooleanProperty(config, "general", "showAboutMessage", true,
        "Shows the about message to the player at first login");

    if (config.hasChanged()) {
      config.save();
    }
  }

  public File getLuaHomeDir() {
    return luaHomeDir;
  }

  public int getLuaTicksLimit() {
    return luaTicksLimit.getValue();
  }

  public int setLuaTicksLimit(int value) {
    return luaTicksLimit.setValue(value, true);
  }

  public boolean shouldShowAboutMessage() {
    return shouldShowAboutMessage.getValue();
  }

  public UserConfig getUserConfig(EntityPlayer player) {
    String uuid = player.getCachedUniqueIdString();
    String configKey = "user-" + uuid;
    UserConfig result = userConfigs.get(configKey);
    if (result == null) {
      result = new UserConfig(config, configKey, uuid, player.getName());
      userConfigs.put(configKey, result);
      if (config.hasChanged()) {
        config.save();
      }
    }
    return result;
  }

  public void clearUserConfigs() {
    Set<String> names = config.getCategoryNames();
    for (String name : names) {
      if (name.startsWith("user-")) {
        ConfigCategory cat = config.getCategory(name);
        config.removeCategory(cat);
      }
    }
    userConfigs.clear();
    if (config.hasChanged()) {
      config.save();
    }
  }

}
