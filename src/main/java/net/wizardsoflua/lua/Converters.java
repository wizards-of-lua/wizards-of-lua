package net.wizardsoflua.lua;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.primitives.Primitives;

import net.minecraft.nbt.NBTBase;
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
import net.wizardsoflua.scribble.LuaApiBase;
import net.wizardsoflua.scribble.LuaApiProxy;

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

  public final <J> List<J> toJavaList(Class<J> type, Object luaObject, int argumentIndex,
      String argumentName, String functionOrPropertyName) throws BadArgumentException {
    return enrichBadArgException(argumentIndex, argumentName, functionOrPropertyName,
        () -> toJavaList(type, luaObject));
  }

  public final <J> Optional<J> toJavaOptional(Class<J> type, @Nullable Object luaObject,
      int argumentIndex, String argumentName, String functionOrPropertyName)
      throws BadArgumentException {
    return enrichBadArgException(argumentIndex, argumentName, functionOrPropertyName,
        () -> toJavaOptional(type, luaObject));
  }

  public final @Nullable <J> J toJavaNullable(Class<J> type, @Nullable Object luaObject,
      int argumentIndex, String argumentName, String functionOrPropertyName)
      throws BadArgumentException {
    return enrichBadArgException(argumentIndex, argumentName, functionOrPropertyName,
        () -> toJavaNullable(type, luaObject));
  }

  public final <J> J toJava(Class<J> type, Object luaObject, int argumentIndex, String argumentName,
      String functionOrPropertyName) throws BadArgumentException {
    return enrichBadArgException(argumentIndex, argumentName, functionOrPropertyName,
        () -> toJava(type, luaObject));
  }

  private <T> T enrichBadArgException(int argumentIndex, String argumentName,
      String functionOrPropertyName, Supplier<T> supplier) throws BadArgumentException {
    requireNonNull(argumentName, "argumentName == null!");
    try {
      return enrichBadArgException(argumentIndex, functionOrPropertyName, supplier);
    } catch (BadArgumentException ex) {
      ex.setArgumentName(argumentName);
      throw ex;
    }
  }

  private <J> J toJava(Class<J> type, Object luaObject, int argumentIndex,
      String functionOrPropertyName) throws BadArgumentException {
    return enrichBadArgException(argumentIndex, functionOrPropertyName,
        () -> toJava(type, luaObject));
  }

  private <T> T enrichBadArgException(int argumentIndex, String functionOrPropertyName,
      Supplier<T> supplier) throws BadArgumentException {
    try {
      return enrichBadArgException(functionOrPropertyName, supplier);
    } catch (BadArgumentException ex) {
      ex.setArgumentIndex(argumentIndex);
      throw ex;
    }
  }

  public final <J> List<J> toJavaList(Class<J> type, Object[] args, String functionOrPropertyName)
      throws ConversionException {
    List<J> result = new ArrayList<>(args.length);
    for (int i = 0; i < args.length; i++) {
      Object arg = args[i];
      int argumentIndex = i + 1;
      J javaObject = toJava(type, arg, argumentIndex, functionOrPropertyName);
      result.add(javaObject);
    }
    return result;
  }

  public final <J> List<J> toJavaList(Class<J> type, Object luaObject,
      String functionOrPropertyName) throws BadArgumentException {
    return enrichBadArgException(functionOrPropertyName, () -> toJavaList(type, luaObject));
  }

  public final <J> Optional<J> toJavaOptional(Class<J> type, @Nullable Object luaObject,
      String functionOrPropertyName) throws BadArgumentException {
    return enrichBadArgException(functionOrPropertyName, () -> toJavaOptional(type, luaObject));
  }

  public final @Nullable <J> J toJavaNullable(Class<J> type, @Nullable Object luaObject,
      String functionOrPropertyName) throws BadArgumentException {
    return enrichBadArgException(functionOrPropertyName, () -> toJavaNullable(type, luaObject));
  }

  public final <J> J toJava(Class<J> type, Object luaObject, String functionOrPropertyName)
      throws BadArgumentException {
    return enrichBadArgException(functionOrPropertyName, () -> toJava(type, luaObject));
  }

  private <T> T enrichBadArgException(String functionOrPropertyName, Supplier<T> supplier)
      throws BadArgumentException {
    requireNonNull(functionOrPropertyName, "functionOrPropertyName == null!");
    try {
      return supplier.get();
    } catch (BadArgumentException ex) {
      ex.setFunctionOrPropertyName(functionOrPropertyName);
      throw ex;
    }
  }

  private <J> List<J> toJavaList(Class<J> type, Object luaObject) throws BadArgumentException {
    Table table = toJava(Table.class, luaObject);
    List<J> result = new ArrayList<>();
    for (Map.Entry<Object, Object> entry : new TableIterable(table)) {
      Object luaValue = entry.getValue();
      J javaValue;
      try {
        javaValue = toJava(type, luaValue);
      } catch (BadArgumentException ex) {
        ByteString key = Conversions.toHumanReadableString(entry.getKey());
        ex.setDetailMessage(
            "table contained illegal value for key '" + key + "' (" + ex.getDetailMessage() + ")");
        throw ex;
      }
      result.add(javaValue);
    }
    return result;
  }

  private <J> Optional<J> toJavaOptional(Class<J> type, Object luaObject) {
    return ofNullable(toJavaNullable(type, luaObject));
  }

  private <J> J toJavaNullable(Class<J> type, Object luaObject) {
    if (luaObject == null) {
      return null;
    }
    return toJava(type, luaObject);
  }

  private <J> J toJava(Class<J> type, Object luaObject) throws BadArgumentException {
    type = Primitives.wrap(type);
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
    if (LuaApiBase.class.isAssignableFrom(type) && luaObject instanceof LuaApiProxy) {
      return ((LuaApiProxy<?, ?>) luaObject).getApi();
    }
    if (type == String.class) {
      return Conversions.javaRepresentationOf(luaObject);
    }
    if (type == Double.class) {
      return castToDouble(luaObject);
    }
    if (type == Float.class) {
      return castToFloat(luaObject);
    }
    if (type == Integer.class) {
      return castToInt(luaObject);
    }
    if (type == Long.class) {
      return castToLong(luaObject);
    }
    if (type == Boolean.class || type == boolean.class) {
      return luaObject;
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

  public final @Nullable Optional<? extends Object> toLuaOptional(@Nullable Object value)
      throws ConversionException {
    return ofNullable(toLuaNullable(value));
  }

  public final @Nullable Object toLuaNullable(@Nullable Object value) throws ConversionException {
    if (value == null) {
      return null;
    }
    return toLua(value);
  }

  public <J> Object toLua(J javaObject) throws ConversionException {
    requireNonNull(javaObject, "value == null!");
    @SuppressWarnings("unchecked")
    Class<J> javaClass = (Class<J>) javaObject.getClass();
    JavaLuaClass<? super J, ?> cls = classLoader.getLuaClassForJavaClassRecursively(javaClass);
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
    if (javaObject instanceof NBTBase) {
      return NbtConverter.toLua((NBTBase) javaObject);
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
