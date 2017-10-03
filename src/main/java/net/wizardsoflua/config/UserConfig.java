package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.UUID;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.wizardsoflua.lua.module.luapath.AddPathFunction;

public class UserConfig {

  private final StringProperty libDir;
  private final StringProperty profile;
  private File luaHomeDir;

  public UserConfig(Configuration config, String key, UUID uuid, String playerName,
      File luaHomeDir) {
    this.luaHomeDir = checkNotNull(luaHomeDir, "luaHomeDir==null!");
    ConfigCategory cat = config.getCategory(key);
    cat.setComment("User-specific configuration for player '" + playerName + "'");
    libDir = new StringProperty(config, key, "libDir", uuid.toString(),
        "The path to the folder containing user-specific Lua modules");
    profile = new StringProperty(config, key, "profile", null,
        "The name of the Lua module in the user's libDir that serves as the user's profile");
  }

  public File getLibDir() {
    return new File(luaHomeDir, libDir.getValue());
  }

  public String getProfile() {
    return profile.getValue();
  }

  public void setProfile(String value) {
    profile.setValue(value, true);
  }

  public String getLibDirPathElement() {
    return getLibDir().getAbsolutePath() + File.separator + AddPathFunction.LUA_EXTENSION_WILDCARD;
  }

}
