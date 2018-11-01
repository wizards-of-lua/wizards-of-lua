package net.wizardsoflua.lua;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import com.google.common.primitives.Primitives;
import net.minecraft.util.IStringSerializable;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.LuaMathOperators;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.wizardsoflua.config.ConversionException;
import net.wizardsoflua.extension.spell.api.SpellScoped;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.JavaToLuaConverter;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.extension.spell.spi.LuaToJavaConverter;
import net.wizardsoflua.lua.module.types.Types;
import net.wizardsoflua.lua.table.TableIterable;

@SpellScoped
public class Converters implements LuaConverters {
  private static final Set<Class<?>> supportedJavaClasses = new HashSet<>();

  /**
   * Returns {@code true} if there exists a spell that can receive an object of the specified java
   * class.
   *
   * @param javaClass
   * @return {@code true} if the specified java class is supported, {@code false} otherwise
   */
  public static boolean isSupported(Class<?> javaClass) {
    return supportedJavaClasses.contains(javaClass);
  }

  private final Provider<Types> typesProvider;
  private final EnumConverter enumConverter = new EnumConverter();

  @Inject
  public Converters(Provider<Types> typesProvider) {
    this.typesProvider = requireNonNull(typesProvider, "typesProvider == null!");
  }

  public Types getTypes() {
    return typesProvider.get();
  }

  @Override
  public final @Nullable <J> List<J> toJavaListNullable(Class<J> type, @Nullable Object luaObject,
      int argumentIndex, String argumentName, String functionOrPropertyName)
      throws BadArgumentException {
    return enrichBadArgException(argumentIndex, argumentName, functionOrPropertyName,
        () -> toJavaListNullable(type, luaObject));
  }

  @Override
  public final <J> List<J> toJavaList(Class<J> type, Object luaObject, int argumentIndex,
      String argumentName, String functionOrPropertyName) throws BadArgumentException {
    return enrichBadArgException(argumentIndex, argumentName, functionOrPropertyName,
        () -> toJavaList(type, luaObject));
  }

  @Override
  public final <J> Optional<J> toJavaOptional(Class<J> type, @Nullable Object luaObject,
      int argumentIndex, String argumentName, String functionOrPropertyName)
      throws BadArgumentException {
    return enrichBadArgException(argumentIndex, argumentName, functionOrPropertyName,
        () -> toJavaOptional(type, luaObject));
  }

  @Override
  public final @Nullable <J> J toJavaNullable(Class<J> type, @Nullable Object luaObject,
      int argumentIndex, String argumentName, String functionOrPropertyName)
      throws BadArgumentException {
    return enrichBadArgException(argumentIndex, argumentName, functionOrPropertyName,
        () -> toJavaNullable(type, luaObject));
  }

  @Override
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

  @Override
  public final <J> List<J> toJavaList(Class<J> type, Object[] args, String functionOrPropertyName)
      throws BadArgumentException {
    List<J> result = new ArrayList<>(args.length);
    for (int i = 0; i < args.length; i++) {
      Object arg = args[i];
      int argumentIndex = i + 1;
      J javaObject = toJava(type, arg, argumentIndex, functionOrPropertyName);
      result.add(javaObject);
    }
    return result;
  }

  @Override
  public final @Nullable <J> List<J> toJavaListNullable(Class<J> type, @Nullable Object luaObject,
      String functionOrPropertyName) throws BadArgumentException {
    return enrichBadArgException(functionOrPropertyName, () -> toJavaListNullable(type, luaObject));
  }

  @Override
  public final <J> List<J> toJavaList(Class<J> type, Object luaObject,
      String functionOrPropertyName) throws BadArgumentException {
    return enrichBadArgException(functionOrPropertyName, () -> toJavaList(type, luaObject));
  }

  @Override
  public final <J> Optional<J> toJavaOptional(Class<J> type, @Nullable Object luaObject,
      String functionOrPropertyName) throws BadArgumentException {
    return enrichBadArgException(functionOrPropertyName, () -> toJavaOptional(type, luaObject));
  }

  @Override
  public final @Nullable <J> J toJavaNullable(Class<J> type, @Nullable Object luaObject,
      String functionOrPropertyName) throws BadArgumentException {
    return enrichBadArgException(functionOrPropertyName, () -> toJavaNullable(type, luaObject));
  }

  @Override
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

  private @Nullable <J> List<J> toJavaListNullable(Class<J> type, @Nullable Object luaObject) {
    if (luaObject == null) {
      return null;
    }
    return toJavaList(type, luaObject);
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

  private <J> Optional<J> toJavaOptional(Class<J> type, @Nullable Object luaObject) {
    return ofNullable(toJavaNullable(type, luaObject));
  }

  private @Nullable <J> J toJavaNullable(Class<J> type, @Nullable Object luaObject) {
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
      BadArgumentException e = badArgument(type, luaObject);
      e.initCause(ex);
      throw e;
    }
  }

  private BadArgumentException badArgument(Class<?> expectedType, Object actualObject) {
    Types types = getTypes();
    String expected = types.getLuaTypeNameForJavaClass(expectedType);
    String actual = types.getLuaTypeNameOfLuaObject(actualObject);
    return new BadArgumentException(expected, actual);
  }

  private final Map<Class<?>, LuaToJavaConverter<?, ?>> luaToJavaConverters = new HashMap<>();
  private final Map<Class<?>, JavaToLuaConverter<?>> javaToLuaConverters = new HashMap<>();

  @Override
  public void registerLuaConverter(LuaConverter<?, ?> converter) throws IllegalArgumentException {
    registerLuaToJavaConverter(converter);
    registerJavaToLuaConverter(converter);
  }

  @Override
  public void registerLuaToJavaConverter(LuaToJavaConverter<?, ?> converter)
      throws IllegalArgumentException {
    Class<?> javaClass = converter.getJavaClass();
    if (luaToJavaConverters.containsKey(javaClass)) {
      throw new IllegalArgumentException(
          "A LuaToJavaConverter for java " + javaClass + " is already registered");
    }
    luaToJavaConverters.put(javaClass, converter);

  }

  @Override
  public void registerJavaToLuaConverter(JavaToLuaConverter<?> converter)
      throws IllegalArgumentException {
    Class<?> javaClass = converter.getJavaClass();
    supportedJavaClasses.add(javaClass);
    if (javaToLuaConverters.containsKey(javaClass)) {
      throw new IllegalArgumentException(
          "A JavaToLuaConverter for java " + javaClass + " is already registered");
    }
    javaToLuaConverters.put(javaClass, converter);
  }

  public <J> LuaToJavaConverter<? super J, ?> getLuaToJavaConverter(Class<J> javaClass) {
    for (Class<? super J> cls = javaClass; cls != null; cls = cls.getSuperclass()) {
      @SuppressWarnings("unchecked")
      LuaToJavaConverter<? super J, ?> result =
          (LuaToJavaConverter<? super J, ?>) luaToJavaConverters.get(cls);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  public <J> JavaToLuaConverter<? super J> getJavaToLuaConverter(Class<J> javaClass) {
    for (Class<? super J> cls = javaClass; cls != null; cls = cls.getSuperclass()) {
      @SuppressWarnings("unchecked")
      JavaToLuaConverter<? super J> result =
          (JavaToLuaConverter<? super J>) javaToLuaConverters.get(cls);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  private <L> Object convertToJava(Object luaObject, LuaToJavaConverter<?, L> converter)
      throws ClassCastException {
    Class<L> luaClass = converter.getLuaClass();
    L luaInstance = luaClass.cast(luaObject);
    return converter.getJavaInstance(luaInstance);
  }

  private Object convertTo(Class<?> javaClass, Object luaObject)
      throws ClassCastException, BadArgumentException {
    LuaToJavaConverter<?, ?> converter = getLuaToJavaConverter(javaClass);
    if (converter != null) {
      return convertToJava(luaObject, converter);
    }
    if (javaClass == String.class) {
      return Conversions.javaRepresentationOf(luaObject);
    }
    if (javaClass == Double.class) {
      return castToDouble(luaObject);
    }
    if (javaClass == Float.class) {
      return castToFloat(luaObject);
    }
    if (javaClass == Integer.class) {
      return castToInt(luaObject);
    }
    if (javaClass == Long.class) {
      return castToLong(luaObject);
    }
    if (javaClass == Boolean.class || javaClass == boolean.class) {
      return luaObject;
    }
    if (Enum.class.isAssignableFrom(javaClass)) {
      String name = (String) Conversions.javaRepresentationOf(luaObject);
      return enumConverter.toJava(javaClass, name);
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
    double doubleValue = number.doubleValue();
    if (Math.abs(doubleValue - result) < 1) {
      return result;
    } else {
      throw new BadArgumentException("number has no float representation");
    }
  }

  private Integer castToInt(Object luaObject) throws ClassCastException, BadArgumentException {
    Integer result = integerValueOf((Number) luaObject);
    if (result != null) {
      return result;
    } else {
      throw new BadArgumentException("number has no int representation");
    }
  }

  public static @Nullable Integer integerValueOf(Object object) {
    if (object instanceof Number) {
      Long index = Conversions.integerValueOf((Number) object);
      if (index != null && index.intValue() == index.longValue()) {
        return index.intValue();
      }
    }
    return null;
  }

  private Long castToLong(Object luaObject) throws ClassCastException, BadArgumentException {
    Long result = Conversions.integerValueOf((Number) luaObject);
    if (result != null) {
      return result;
    } else {
      throw new BadArgumentException("number has no long representation");
    }
  }

  @Override
  public final @Nullable Optional<? extends Object> toLuaOptional(@Nullable Object value)
      throws ConversionException {
    return ofNullable(toLuaNullable(value));
  }

  @Override
  public final @Nullable Object toLuaNullable(@Nullable Object value) throws ConversionException {
    if (value == null) {
      return null;
    }
    return toLua(value);
  }

  @Override
  public <J> Object toLua(J javaObject) throws ConversionException {
    requireNonNull(javaObject, "javaObject == null!");
    @SuppressWarnings("unchecked")
    Class<J> javaClass = (Class<J>) javaObject.getClass();
    JavaToLuaConverter<? super J> converter = getJavaToLuaConverter(javaClass);
    if (converter != null) {
      return converter.getLuaInstance(javaObject);
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
    if (javaObject instanceof Table) {
      return javaObject;
    }
    if (javaObject instanceof ByteString) {
      return javaObject;
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
