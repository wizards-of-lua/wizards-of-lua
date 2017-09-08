package net.wizardsoflua.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

class BooleanProperty {

  private final Configuration config;

  private final Property property;
  private boolean value;

  public BooleanProperty(Configuration config, String category, String name, boolean defaultValue,
      String comment) {
    this.config = config;
    property = config.get(category, name, defaultValue);
    property.setLanguageKey(name);
    property.setComment(comment + " [default: " + defaultValue + "]");
    setValue(property.getBoolean(), false);
  }

  public boolean getValue() {
    return value;
  }

  public boolean setValue(boolean newValue, boolean saveNow) {
    value = newValue;
    property.set(value);
    if (saveNow && config.hasChanged()) {
      config.save();
    }
    return value;
  }

}
