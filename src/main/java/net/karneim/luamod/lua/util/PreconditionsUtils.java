package net.karneim.luamod.lua.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.sandius.rembulan.ByteString;

public class PreconditionsUtils {
  public static <T> T checkType(Object arg, Class<T> type) {
    checkNotNull(type, "type == null!");
    checkArgument(arg != null, "Expected %s but got nil", type.getSimpleName());
    return checkTypeNullable(arg, type);
  }

  public static <T> T checkTypeNullable(@Nullable Object arg, Class<T> type) {
    checkNotNull(type, "type == null!");
    if (arg == null) {
      return null;
    }
    checkArgument(type.isInstance(arg), "Expected %s but got %s", type.getSimpleName(),
        arg.getClass().getSimpleName());
    return type.cast(arg);
  }

  public static <T> T checkType(int argIndex, Object arg, Class<T> type) {
    checkNotNull(type, "type == null!");
    checkArgument(arg != null, "Expected %s for argument %s but got nil", type.getSimpleName(),
        argIndex);
    return checkTypeNullable(argIndex, arg, type);
  }

  public static <T> T checkTypeNullable(int argIndex, @Nullable Object arg, Class<T> type) {
    checkNotNull(type, "type == null!");
    if (arg == null) {
      return null;
    }
    checkArgument(type.isInstance(arg), "Expected %s for argument %s but got %s",
        type.getSimpleName(), argIndex, arg.getClass().getSimpleName());
    return type.cast(arg);
  }

  public static String checkTypeString(int argIndex, Object arg) {
    checkArgument(arg != null, "Expected String for argument %s but got nil", argIndex);
    return checkTypeStringNullable(argIndex, arg);
  }

  public static String checkTypeStringNullable(int argIndex, Object arg) {
    if (arg == null) {
      return null;
    }
    if (arg instanceof String) {
      return (String) arg;
    }
    if (arg instanceof ByteString) {
      return arg.toString();
    }
    throw new IllegalArgumentException(String.format("Expected String for argument %s but got %s",
        argIndex, arg.getClass().getSimpleName()));
  }
}
