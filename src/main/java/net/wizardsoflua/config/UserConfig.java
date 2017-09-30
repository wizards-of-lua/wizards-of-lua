package net.wizardsoflua.config;

import com.google.common.base.Preconditions;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class UserConfig {

  @SuppressWarnings("unused")
  private Configuration config;
  private final StringProperty libDir;
  private final StringProperty profile;

  public UserConfig(Configuration config, String key, String userGuid, String playerName) {
    this.config = Preconditions.checkNotNull(config, "config==null!");
    ConfigCategory cat = config.getCategory(key);
    cat.setComment("User-specific configuration for player '" + playerName + "'");
    libDir = new StringProperty(config, key, "libDir", userGuid,
        "The path to the folder containing user-specific Lua modules");
    profile = new StringProperty(config, key, "profile", null,
        "The name of the Lua module in the user's libDir that serves as the user's profile");
  }

  public String getLibDir() {
    return libDir.getValue();
  }

  public String getProfile() {
    return profile.getValue();
  }

  public void setProfile(String value) {
    profile.setValue(value, true);
  }

}
