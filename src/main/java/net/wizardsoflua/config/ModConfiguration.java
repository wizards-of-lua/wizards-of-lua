package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.wizardsoflua.lua.module.luapath.AddPathFunction;

public class ModConfiguration {

  private static final String HOME = "home";
  private static final String SHARED_HOME = "shared";

  private static final String USER_KEY_PREFIX = "user-";

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

  private final Map<String, UserConfig> userConfigsByKey = new HashMap<>();

  public ModConfiguration(File configDir, Configuration config) {
    this.configDir = checkNotNull(configDir, "configDir==null!");
    this.config = checkNotNull(config, "config==null!");;
    luaHomeDir = new File(configDir, HOME);

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

  public File getSharedLibDir() {
    return new File(getLuaHomeDir(), SHARED_HOME);
  }

  public String getSharedLuaPath() {
    return getSharedLibDir().getAbsolutePath() + File.separator
        + AddPathFunction.LUA_EXTENSION_WILDCARD;
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

  public @Nullable UserConfig getUserConfig(UUID uuid) {
    String configKey = getKey(uuid);
    UserConfig result = userConfigsByKey.get(configKey);
    return result;
  }

  public UserConfig getUserConfig(EntityPlayer player) {
    return getUserConfig(player.getGameProfile());
  }

  public UserConfig getUserConfig(GameProfile profile) {
    UUID uuid = profile.getId();
    UserConfig result = getUserConfig(uuid);
    if (result == null) {
      String name = profile.getName();
      String key = getKey(uuid);
      result = new UserConfig(this.config, key, uuid, name, getLuaHomeDir());
      userConfigsByKey.put(key, result);
      if (config.hasChanged()) {
        config.save();
      }
    }
    return result;
  }

  private String getKey(UUID uuid) {
    return USER_KEY_PREFIX + uuid;
  }

  public void clearUserConfigs() {
    Set<String> names = config.getCategoryNames();
    for (String name : names) {
      if (name.startsWith(USER_KEY_PREFIX)) {
        ConfigCategory cat = config.getCategory(name);
        config.removeCategory(cat);
      }
    }
    userConfigsByKey.clear();
    if (config.hasChanged()) {
      config.save();
    }
  }

}
