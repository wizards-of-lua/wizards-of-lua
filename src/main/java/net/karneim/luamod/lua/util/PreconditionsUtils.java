package net.karneim.luamod.lua.util;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

public class PreconditionsUtils {
  public static <T> T checkType(Object object, Class<T> type) {
    checkNotNull(type, "type == null!");
    if (object == null)
      throw new IllegalArgumentException(
          String.format("%s expected but got nil!", type.getSimpleName()));
    return checkTypeNullable(object, type);
  }

  public static @Nullable <T> T checkTypeNullable(@Nullable Object object, Class<T> type) {
    checkNotNull(type, "type == null!");
    if (object == null)
      return null;
    if (type.isInstance(object))
      return type.cast(object);
    throw new IllegalArgumentException(String.format("%s expected but %s!", type.getSimpleName(),
        object.getClass().getSimpleName()));
  }
}
