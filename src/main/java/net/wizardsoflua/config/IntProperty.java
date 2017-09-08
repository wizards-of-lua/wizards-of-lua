package net.wizardsoflua.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

class IntProperty {

  private final Configuration config;
  private int minValue;
  private int maxValue;

  private final Property property;
  private int value;

  public IntProperty(Configuration config, String category, String name, int defaultValue,
      int minValue, int maxValue, String comment) {
    this.config = config;
    this.minValue = minValue;
    this.maxValue = maxValue;
    property = config.get(category, name, defaultValue);
    property.setLanguageKey(name);
    property.setMinValue(minValue);
    property.setMaxValue(maxValue);
    property.setComment(
        comment + " [range: " + minValue + " ~ " + maxValue + ", default: " + defaultValue + "]");
    setValue(property.getInt(), false);
  }

  public int getValue() {
    return value;
  }

  public int setValue(int newValue, boolean saveNow) {
    value = clamp(newValue, minValue, maxValue);
    property.set(value);
    if (saveNow && config.hasChanged()) {
      config.save();
    }
    return value;
  }

  private int clamp(int value, int min, int max) {
    if (value > max) {
      return max;
    }
    if (value < min) {
      return min;
    }
    return value;
  }

}
