package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import javax.annotation.Nullable;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;

// TODO refactor WolConversions and Converters and Types, since they do some equal things.
public class WolConversions {

  public static @Nullable <T> T toJavaNullable(Class<T> type, @Nullable Object value) {
    if (value == null) {
      return null;
    }
    return toJava(type, value, null);
  }

  public static <T> Optional<T> toJavaOptional(Class<T> type, @Nullable Object value) {
    return Optional.ofNullable(toJavaNullable(type, value));
  }

  public static @Nullable <T> T toJavaNullable(Class<T> type, @Nullable Object value,
      @Nullable String name) {
    if (value == null) {
      return null;
    }
    return toJava(type, value, name);
  }

  public static <T> T toJava(Class<T> type, Object value) {
    return toJava(type, value);
  }

  @SuppressWarnings("unchecked")
  public static <T> T toJava(Class<T> type, Object value, @Nullable String name) {
    if (name == null) {
      name = "argument";
    }
    checkNotNull(value, name + "==null!");
    if (Boolean.class == type) {
      checkCondition(value instanceof Boolean, type, value, name);
      Boolean result = (Boolean) value;
      return (T) result;
    }
    if (Integer.class == type) {
      Long longV = Conversions.integerValueOf(value);
      checkCondition(longV != null, type, value, name);
      return (T) Integer.valueOf(longV.intValue());
    }
    if (Long.class == type) {
      Long longV = Conversions.integerValueOf(value);
      checkCondition(longV != null, type, value, name);
      return (T) longV;
    }
    if (Number.class == type) {
      Number numV = Conversions.numericalValueOf(value);
      checkCondition(numV != null, type, value, name);
      return (T) numV;
    }
    if (String.class == type) {
      ByteString byteStrV = Conversions.stringValueOf(value);
      checkCondition(byteStrV != null, type, value, name);
      return (T) byteStrV.toString();
    }
    return null;
  }

  private static <T> void checkCondition(boolean condition, Class<T> type, Object value,
      String name) {
    checkArgument(condition, "value '%s' of '%s' is not an instance of %s!", value, name,
        type.getSimpleName());
  }

}
