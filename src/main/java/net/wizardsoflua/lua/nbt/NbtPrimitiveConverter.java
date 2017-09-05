package net.wizardsoflua.lua.nbt;

import javax.annotation.Nullable;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import net.minecraft.util.IStringSerializable;
import net.sandius.rembulan.Conversions;

public class NbtPrimitiveConverter {

  public static Object toLua(Object obj) {
    if (obj instanceof IStringSerializable) {
      IStringSerializable s = (IStringSerializable) obj;
      return s.getName();
    }
    if (obj instanceof Enum) {
      Enum<?> e = (Enum<?>) obj;
      return e.name();
    }
    if (obj instanceof String) {
      String str = (String) obj;
      if ("true".equals(obj)) {
        return true;
      }
      if ("false".equals(obj)) {
        return false;
      }
      Object result = Ints.tryParse(str);
      if (result != null) {
        return result;
      }
      result = Doubles.tryParse(str);
      if (result != null) {
        return result;
      }
    }
    return Conversions.canonicalRepresentationOf(obj);
  }

  @SuppressWarnings("unchecked")
  public static @Nullable <T extends Comparable<T>> T toJava(Class<?> valueClass,
      @Nullable Object luaValue) {
    if (luaValue == null) {
      return null;
    }
    if (valueClass.equals(Boolean.class)) {
      if (luaValue == Boolean.TRUE) {
        return (T) Boolean.TRUE;
      }
      if (luaValue == Boolean.FALSE) {
        return (T) Boolean.FALSE;
      }
      throw new IllegalArgumentException(
          String.format("Unexpected value '%s' for valueClass %s", luaValue, valueClass));
    }
    if (IStringSerializable.class.isAssignableFrom(valueClass)) {
      if (Enum.class.isAssignableFrom(valueClass)) {
        String enumValueName = String.valueOf(luaValue);
        Class<? extends Enum<?>> eClass = (Class<? extends Enum<?>>) valueClass;
        Enum<?>[] values = eClass.getEnumConstants();

        for (Enum<?> value : values) {
          IStringSerializable sObj = (IStringSerializable) value;
          if (sObj.getName().equals(enumValueName)) {
            return (T) value;
          }
        }
        throw new IllegalArgumentException(String.format(
            "Unexpected name '%s' for element of valueClass %s", enumValueName, valueClass));
      } else {
        throw new IllegalArgumentException(String.format("Unexpected valueClass %s", valueClass));
      }
    }
    if (Enum.class.isAssignableFrom(valueClass)) {
      String enumValueName = String.valueOf(luaValue);
      Class<? extends Enum<?>> eClass = (Class<? extends Enum<?>>) valueClass;
      Enum<?>[] values = eClass.getEnumConstants();
      for (Enum<?> value : values) {
        if (value.name().equals(enumValueName)) {
          return (T) value;
        }
      }
      throw new IllegalArgumentException(String
          .format("Unexpected name '%s' for element of valueClass %s", enumValueName, valueClass));
    }
    if (Number.class.isAssignableFrom(valueClass)) {
      Number num = Conversions.numericalValueOf(luaValue);
      if (valueClass.equals(Integer.class)) {
        return (T) Integer.valueOf(num.intValue());
      }
      if (valueClass.equals(Double.class)) {
        return (T) Double.valueOf(num.doubleValue());
      }
    }
    throw new IllegalArgumentException(String.format("Unexpected valueClass %s", valueClass));
  }
}
