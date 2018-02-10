package net.wizardsoflua.lua;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.util.IStringSerializable;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.LuaMathOperators;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.wizardsoflua.config.ConversionException;
import net.wizardsoflua.lua.classes.JavaLuaClass;
import net.wizardsoflua.lua.classes.LuaClass;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.data.TableData;
import net.wizardsoflua.lua.data.TableDataConverter;
import net.wizardsoflua.lua.module.types.Types;
import net.wizardsoflua.lua.nbt.NbtConverter;
import net.wizardsoflua.lua.table.TableIterable;

public class Converters {
  private final LuaClassLoader classLoader;
  private final NbtConverter nbtConverter;
  private final TableDataConverter tableDataConverter;
  private final EnumConverter enumConverter = new EnumConverter();

  public Converters(LuaClassLoader classLoader) {
    this.classLoader = requireNonNull(classLoader, "classLoader == null!");
    nbtConverter = new NbtConverter(classLoader);
    tableDataConverter = new TableDataConverter(classLoader);
  }

  public NbtConverter getNbtConverter() {
    return nbtConverter;
  }

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

  private <T> T toJava(Class<T> type, Object luaObject) throws BadArgumentException {
    if (luaObject == null) {
      throw badArgument(type, luaObject);
    }
    try {
      Object result = convertTo(type, luaObject);
      return type.cast(result);
    } catch (ClassCastException ex) {
      throw badArgument(type, luaObject);
    }
  }

  private BadArgumentException badArgument(Class<?> expectedType, Object actualObject) {
    Types types = classLoader.getTypes();
    String expected = types.getTypename(expectedType);
    String actual = types.getTypename(actualObject);
    return new BadArgumentException(expected, actual);
  }

  private Object convertTo(Class<?> type, Object luaObject)
      throws ClassCastException, BadArgumentException {
    if (LuaClassLoader.isSupported(type) && luaObject instanceof Table) {
      Table table = (Table) luaObject;
      LuaClass luaClass = classLoader.getLuaClassOf(table);
      if (luaClass instanceof JavaLuaClass) {
        return ((JavaLuaClass<?, ?>) luaClass).getJavaInstance(table);
      }
    }
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

  public final @Nullable <T> Optional<? extends Object> toLuaOptional(@Nullable T value)
      throws ConversionException {
    return ofNullable(toLuaNullable(value));
  }

  public final @Nullable <T> Object toLuaNullable(@Nullable T value) throws ConversionException {
    if (value == null) {
      return null;
    }
    return toLua(value);
  }

  public <T> Object toLua(T javaObject) throws ConversionException {
    requireNonNull(javaObject, "value == null!");
    @SuppressWarnings("unchecked")
    Class<T> javaClass = (Class<T>) javaObject.getClass();
    JavaLuaClass<? super T, ?> cls = classLoader.getLuaClassForJavaClassRecursively(javaClass);
    if (cls != null) {
      return cls.getLuaInstance(javaObject);
    }
    if (javaObject instanceof TableData) {
      TableData data = (TableData) javaObject;
      return tableDataConverter.toLua(data);
    }
    if (javaObject instanceof Iterable<?>) {
      DefaultTable result = new DefaultTable();
      int index = 0;
      for (Object value : (Iterable<?>) javaObject) {
        result.rawset(++index, toLua(value));
      }
      return result;
    }
    if (javaObject instanceof IStringSerializable) {
      return ByteString.of(((IStringSerializable) javaObject).getName());
    }
    if (javaObject instanceof Enum) {
      Enum<?> enumValue = (Enum<?>) javaObject;
      Object result = enumConverter.toLua(enumValue);
      if (result != null) {
        return result;
      }
      return ByteString.of(enumValue.name());
    }
    if (javaObject instanceof String) {
      return ByteString.of((String) javaObject);
    }
    if (javaObject instanceof Number) {
      return javaObject;
    }
    if (javaObject instanceof Boolean) {
      return javaObject;
    }
    throw new ConversionException(
        format("Can't convert value! Unsupported type: %s", javaObject.getClass().getName()));
  }
}
