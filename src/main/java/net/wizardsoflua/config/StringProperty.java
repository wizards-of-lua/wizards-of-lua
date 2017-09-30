package net.wizardsoflua.config;

import javax.annotation.Nullable;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

class StringProperty {

  private final Configuration config;

  private final Property property;
  private String value;

  public StringProperty(Configuration config, String category, String name,
      @Nullable String defaultValue, String comment) {
    this.config = config;
    if (defaultValue == null) {
      defaultValue = "";
    }
    this.property = config.get(category, name, defaultValue);
    property.setLanguageKey(name);
    if (defaultValue != null) {
      property.setComment(comment + " [default: " + defaultValue + "]");
    }
    setValue(property.getString(), false);
  }

  public String getValue() {
    return value;
  }

  public String setValue(String newValue, boolean saveNow) {
    if ("".equals(newValue)) {
      newValue = null;
    }
    value = newValue;
    if (newValue == null) {
      property.set("");
    } else {
      property.set(newValue);
    }
    if (saveNow && config.hasChanged()) {
      config.save();
    }
    return value;
  }

}
