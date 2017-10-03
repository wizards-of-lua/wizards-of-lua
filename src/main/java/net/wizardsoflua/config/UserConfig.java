package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.UUID;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.wizardsoflua.lua.module.luapath.AddPathFunction;

public class UserConfig {

  private final StringProperty libDir;
  private final StringProperty required;
  private File luaHomeDir;

  public UserConfig(Configuration config, String key, UUID uuid, String playerName,
      File luaHomeDir) {
    this.luaHomeDir = checkNotNull(luaHomeDir, "luaHomeDir==null!");
    ConfigCategory cat = config.getCategory(key);
    cat.setComment("User-specific configuration for player '" + playerName + "'");
    libDir = new StringProperty(config, key, "libDir", uuid.toString(),
        "The path to the folder containing user-specific Lua modules");
    required = new StringProperty(config, key, "required", null,
        "The name of the Lua module that is automatically required by any of the user's spells. It serves as the user's profile.");
  }

  public File getLibDir() {
    return new File(luaHomeDir, libDir.getValue());
  }

  public String getRequireModule() {
    return required.getValue();
  }

  public void setRequiredModule(String value) {
    required.setValue(value, true);
  }

  public String getLibDirPathElement() {
    return getLibDir().getAbsolutePath() + File.separator + AddPathFunction.LUA_EXTENSION_WILDCARD;
  }

}