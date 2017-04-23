package net.karneim.luamod.lua.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

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
    checkArgument(type.isInstance(arg), "Expected %s for argument%s but got %s",
        type.getSimpleName(), argIndex, arg.getClass().getSimpleName());
    return type.cast(arg);
  }
}
