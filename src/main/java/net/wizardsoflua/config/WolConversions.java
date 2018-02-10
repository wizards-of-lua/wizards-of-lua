package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.LuaMathOperators;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.wizardsoflua.lua.BadArgumentException;
import net.wizardsoflua.lua.EnumConverter;
import net.wizardsoflua.lua.table.TableIterable;

public class WolConversions {
  protected final EnumConverter enumConverter = new EnumConverter();

  public final @Nullable <T> Optional<? extends Object> toLuaOptional(@Nullable T value,
      String name) throws ConversionException {
    checkNotNull(name, "name==null!");
    try {
      return toLuaOptional(value);
    } catch (Exception e) {
      throw new ConversionException(format("Can't convert '%s' argument!", name), e);
    }
  }

  public final @Nullable <T> Optional<? extends Object> toLuaOptional(@Nullable T value)
      throws ConversionException {
    return Optional.ofNullable(toLuaNullable(value));
  }

  public final @Nullable <T> Object toLuaNullable(@Nullable T value, String name)
      throws ConversionException {
    checkNotNull(name, "name==null!");
    try {
      return toLuaNullable(value);
    } catch (ConversionException e) {
      throw new ConversionException(format("Can't convert '%s' argument!", name), e);
    }
  }

  public final @Nullable <T> Object toLuaNullable(@Nullable T value) throws ConversionException {
    if (value == null) {
      return null;
    }
    return toLua(value);
  }

  public final <T> Object toLua(T value, String name) throws ConversionException {
    checkNotNull(name, "name==null!");
    try {
      return toLua(value);
    } catch (ConversionException e) {
      throw new ConversionException(format("Can't convert '%s' argument!", name), e);
    }
  }

  public final <T, I extends Iterable<? extends T>> Table toLuaIterable(I values)
      throws ConversionException {
    DefaultTable result = new DefaultTable();
    int index = 0;
    for (T value : values) {
      index++;
      result.rawset(index, toLua(value));
    }
    return result;
  }

  public <T> Object toLua(T value) throws ConversionException {
    checkNotNull(value, "value==null!");
    if (value instanceof String) {
      return ByteString.of((String) value);
    }
    if (value instanceof Number) {
      return value;
    }
    if (value instanceof Boolean) {
      return value;
    }
    throw new ConversionException(
        format("Can't convert value! Unsupported type: %s", value.getClass().getName()));
  }

  ///////////

  public final <J> Collection<J> toJavaCollection(Class<J> type, Object[] args,
      String functionOrPropertyName) throws ConversionException {
    Collection<J> result = new ArrayList<>(args.length);
    for (int i = 0; i < args.length; i++) {
      Object arg = args[i];
      int argumentIndex = i + 1;
      J javaObject = toJava(type, arg, argumentIndex, functionOrPropertyName);
      result.add(javaObject);
    }
    return result;
  }

  public final <J> Collection<J> toJavaCollection(Class<J> type, Object luaObject,
      String functionOrPropertyName) throws ConversionException {
    Table table = toJava(Table.class, luaObject, functionOrPropertyName);
    Collection<J> result = new ArrayList<>();
    for (Map.Entry<Object, Object> entry : new TableIterable(table)) {
      Object key = entry.getKey();
      String argumentName = Conversions.toHumanReadableString(key).toString();
      Object luaValue = entry.getValue();
      J javaValue = toJava(type, luaValue, argumentName, functionOrPropertyName);
      result.add(javaValue);
    }
    return result;
  }

  public final <T> Optional<T> toJavaOptional(Class<T> type, @Nullable Object luaObject,
      int argumentIndex, String argumentName, String functionOrPropertyName)
      throws BadArgumentException {
    return ofNullable(
        toJavaNullable(type, luaObject, argumentIndex, argumentName, functionOrPropertyName));
  }

  public final @Nullable <T> T toJavaNullable(Class<T> type, @Nullable Object luaObject,
      int argumentIndex, String argumentName, String functionOrPropertyName)
      throws BadArgumentException {
    requireNonNull(argumentName, "argumentName == null!");
    requireNonNull(functionOrPropertyName, "functionOrPropertyName == null!");
    if (luaObject == null) {
      return null;
    }
    return toJava(type, luaObject, argumentIndex, argumentName, functionOrPropertyName);
  }

  public final <T> T toJava(Class<T> type, Object luaObject, int argumentIndex, String argumentName,
      String functionOrPropertyName) throws BadArgumentException {
    try {
      return toJava(type, luaObject, argumentName, functionOrPropertyName);
    } catch (BadArgumentException ex) {
      ex.setArgumentIndex(argumentIndex);
      throw ex;
    }
  }

  public final <T> Optional<T> toJavaOptional(Class<T> type, @Nullable Object luaObject,
      String argumentName, String functionOrPropertyName) throws BadArgumentException {
    return ofNullable(toJavaNullable(type, luaObject, argumentName, functionOrPropertyName));
  }

  public final @Nullable <T> T toJavaNullable(Class<T> type, @Nullable Object luaObject,
      String argumentName, String functionOrPropertyName) throws BadArgumentException {
    requireNonNull(argumentName, "argumentName == null!");
    requireNonNull(functionOrPropertyName, "functionOrPropertyName == null!");
    if (luaObject == null) {
      return null;
    }
    return toJava(type, luaObject, argumentName, functionOrPropertyName);
  }

  public final <T> T toJava(Class<T> type, Object luaObject, String argumentName,
      String functionOrPropertyName) throws BadArgumentException {
    requireNonNull(argumentName, "argumentName == null!");
    try {
      return toJava(type, luaObject, functionOrPropertyName);
    } catch (BadArgumentException ex) {
      ex.setArgumentName(argumentName);
      throw ex;
    }
  }

  public final <T> Optional<T> toJavaOptional(Class<T> type, @Nullable Object luaObject,
      int argumentIndex, String functionOrPropertyName) throws BadArgumentException {
    return ofNullable(toJavaNullable(type, luaObject, argumentIndex, functionOrPropertyName));
  }

  public final @Nullable <T> T toJavaNullable(Class<T> type, @Nullable Object luaObject,
      int argumentIndex, String functionOrPropertyName) throws BadArgumentException {
    requireNonNull(functionOrPropertyName, "functionOrPropertyName == null!");
    if (luaObject == null) {
      return null;
    }
    return toJava(type, luaObject, argumentIndex, functionOrPropertyName);
  }

  public final <T> T toJava(Class<T> type, Object luaObject, int argumentIndex,
      String functionOrPropertyName) throws BadArgumentException {
    try {
      return toJava(type, luaObject, functionOrPropertyName);
    } catch (BadArgumentException ex) {
      ex.setArgumentIndex(argumentIndex);
      throw ex;
    }
  }

  public final <T> Optional<T> toJavaOptional(Class<T> type, @Nullable Object luaObject,
      String functionOrPropertyName) throws BadArgumentException {
    return ofNullable(toJavaNullable(type, luaObject, functionOrPropertyName));
  }

  public final @Nullable <T> T toJavaNullable(Class<T> type, @Nullable Object luaObject,
      String functionOrPropertyName) throws BadArgumentException {
    requireNonNull(functionOrPropertyName, "functionOrPropertyName == null!");
    if (luaObject == null) {
      return null;
    }
    return toJava(type, luaObject, functionOrPropertyName);
  }

  public final <T> T toJava(Class<T> type, Object luaObject, String functionOrPropertyName)
      throws BadArgumentException {
    requireNonNull(functionOrPropertyName, "functionOrPropertyName == null!");
    try {
      return toJava(type, luaObject);
    } catch (BadArgumentException ex) {
      ex.setFunctionOrPropertyName(functionOrPropertyName);
      throw ex;
    }
  }

  @Deprecated
  @SuppressWarnings("unchecked")
  protected <T> T toJava(Class<T> type, Object luaObject) throws ConversionException {
    checkNotNull(luaObject, "luaObject == null!");
    if (Boolean.class == type) {
      if (luaObject instanceof Boolean) {
        return (T) luaObject;
      }
      throw new ConversionException(format("Can't convert value! Boolean expected, but got: %s",
          luaObject.getClass().getName()));
    }
    if (Integer.class == type) {
      Long longV = Conversions.integerValueOf(luaObject);
      if (longV != null) {
        return (T) Integer.valueOf(longV.intValue());
      }
      throw new ConversionException(format("Can't convert value! Integer expected, but got: %s",
          luaObject.getClass().getName()));
    }
    if (Long.class == type) {
      Long longV = Conversions.integerValueOf(luaObject);
      if (longV != null) {
        return (T) longV;
      }
      throw new ConversionException(format("Can't convert value! Long expected, but got: %s",
          luaObject.getClass().getName()));
    }
    if (Number.class == type) {
      Number numV = Conversions.numericalValueOf(luaObject);
      if (numV != null) {
        return (T) numV;
      }
      throw new ConversionException(format("Can't convert value! Number expected, but got: %s",
          luaObject.getClass().getName()));
    }
    if (String.class == type) {
      ByteString byteStrV = Conversions.stringValueOf(luaObject);
      if (byteStrV != null) {
        return (T) byteStrV.toString();
      }
      throw new ConversionException(format("Can't convert value! String expected, but got: %s",
          luaObject.getClass().getName()));
    }
    throw new IllegalArgumentException(String.format("Unsupported type %s!", type));
  }

  protected Object convertTo(Class<?> type, Object luaObject)
      throws ClassCastException, BadArgumentException {
    if (type == String.class) {
      return Conversions.javaRepresentationOf(luaObject);
    }
    if (type == Double.class || type == double.class) {
      return castToDouble(luaObject);
    }
    if (type == Float.class || type == float.class) {
      return castToFloat(luaObject);
    }
    if (type == Integer.class || type == int.class) {
      return castToInt(luaObject);
    }
    if (type == Long.class || type == long.class) {
      return castToLong(luaObject);
    }
    if (Enum.class.isAssignableFrom(type)) {
      String name = (String) Conversions.javaRepresentationOf(luaObject);
      return enumConverter.toJava(type, name);
    }
    return luaObject;
  }

  private Double castToDouble(Object luaObject) throws ClassCastException, BadArgumentException {
    if (luaObject instanceof Long) {
      Long l = (Long) luaObject;
      if (LuaMathOperators.hasExactFloatRepresentation(l.longValue())) {
        return l.doubleValue();
      } else {
        throw new BadArgumentException("number has no double representation");
      }
    }
    return Conversions.floatValueOf((Number) luaObject);
  }

  private Float castToFloat(Object luaObject) throws ClassCastException, BadArgumentException {
    Number number = (Number) luaObject;
    float result = number.floatValue();
    if ((double) result == number.doubleValue()) {
      return result;
    } else {
      throw new BadArgumentException("number has no float representation");
    }
  }

  private Integer castToInt(Object luaObject) throws ClassCastException, BadArgumentException {
    Long result = Conversions.integerValueOf((Number) luaObject);
    if (result != null && result.intValue() == result.longValue()) {
      return result.intValue();
    } else {
      throw new BadArgumentException("number has no int representation");
    }
  }

  private Long castToLong(Object luaObject) throws ClassCastException, BadArgumentException {
    Long result = Conversions.integerValueOf((Number) luaObject);
    if (result != null) {
      return result;
    } else {
      throw new BadArgumentException("number has no long representation");
    }
  }
}
