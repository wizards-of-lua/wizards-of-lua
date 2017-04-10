package net.karneim.luamod.config;

import java.io.File;

import javax.annotation.Nullable;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ModConfiguration {

  private Configuration config;

  public ModConfiguration(File configFile) {
    config = new Configuration(configFile);
    config.load();
  }

  public @Nullable String getStringOrNull(String category, String key) {
    return getStringOrDefault(category, key, null);
  }

  public @Nullable String getStringOrDefault(String category, String key, @Nullable String defaultValue) {
    Property property = config.get(category, key, defaultValue);
    if (property == null) {
      return null;
    }
    return property.getString();
  }

  public void setString(String category, String key, String value) {
    if (value == null) {
      ConfigCategory cat = config.getCategory(category);
      cat.remove(key);
    } else {
      Property p = config.get(category, key, "");
      p.set(value);
    }
  }

  public void save() {
    config.save();
  }

}
