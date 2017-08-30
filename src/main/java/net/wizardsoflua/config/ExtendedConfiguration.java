package net.wizardsoflua.config;

import java.io.File;

import javax.annotation.Nullable;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ExtendedConfiguration extends Configuration {

  public ExtendedConfiguration(File file) {
    super(file);
  }

  public @Nullable String getStringOrNull(String category, String key) {
    return getStringOrDefault(category, key, null);
  }

  public @Nullable String getStringOrDefault(String category, String key,
      @Nullable String defaultValue) {
    Property property = get(category, key, defaultValue);
    if (property == null) {
      return null;
    }
    return property.getString();
  }

  public void setString(String category, String key, String value) {
    if (value == null) {
      ConfigCategory cat = getCategory(category);
      cat.remove(key);
    } else {
      Property p = get(category, key, "");
      p.set(value);
    }
  }

  public void setInt(String category, String key, int value) {
    Property p = get(category, key, 0);
    p.set(value);
  }

}
