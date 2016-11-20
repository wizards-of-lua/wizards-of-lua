package net.karneim.luamod.config;

import java.io.File;

import javax.annotation.Nullable;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ModConfiguration {

  private Configuration config;

  public ModConfiguration(File configFile) {
    config = new Configuration(configFile);
    config.load();
  }

  public @Nullable String getStringOrNull(String category, String key) {
    Property property = config.get(category, key, (String) null);
    if (property == null) {
      return null;
    }
    return property.getString();
  }

  public void setString(String category, String key, String value) {
    Property p = config.get(category, key, value);
    if ( p != null) {
      p.set(value);
    }
  }

  public void save() {
    config.save();
  }


}
