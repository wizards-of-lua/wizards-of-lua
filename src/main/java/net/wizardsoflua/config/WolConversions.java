package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.wizardsoflua.lua.table.TableIterable;

public class WolConversions {

  public @Nullable Table castToTableNullable(Object luaObj, String name)
      throws ConversionException {
    checkNotNull(name, "name==null!");
    try {
      return castToTableNullable(luaObj);
    } catch (Exception e) {
      throw new ConversionException(format("Can't convert '%s' argument!", name), e);
    }
  }

  public @Nullable Table castToTableNullable(Object luaObj) throws ConversionException {
    if (luaObj == null) {
      return null;
    }
    return castToTable(luaObj);
  }

  public Table castToTable(Object luaObj, String name) throws ConversionException {
    checkNotNull(name, "name==null!");
    try {
      return castToTable(luaObj);
    } catch (Exception e) {
      throw new ConversionException(format("Can't convert '%s' argument!", name), e);
    }
  }

  public Table castToTable(Object luaObj) throws ConversionException {
    checkNotNull(luaObj, "luaObj==null!");
    if (luaObj instanceof Table) {
      return (Table) luaObj;
    }
    throw new ConversionException(
        format("Table expected, but got: %s", luaObj.getClass().getName()));
  }

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

  public final <T> Optional<T> toJavaOptional(Class<T> type, @Nullable Object luaObj, String name)
      throws ConversionException {
    checkNotNull(name, "name==null!");
    try {
      return toJavaOptional(type, luaObj);
    } catch (Exception e) {
      throw new ConversionException(format("Can't convert '%s' argument!", name), e);
    }
  }

  public final <T> Optional<T> toJavaOptional(Class<T> type, @Nullable Object luaObj)
      throws ConversionException {
    return Optional.ofNullable(toJavaNullable(type, luaObj));
  }

  public final @Nullable <T> T toJavaNullable(Class<T> type, @Nullable Object luaObj, String name)
      throws ConversionException {
    checkNotNull(name, "name==null!");
    try {
      return toJavaNullable(type, luaObj);
    } catch (ConversionException e) {
      throw new ConversionException(format("Can't convert '%s' argument!", name), e);
    }
  }

  public final @Nullable <T> T toJavaNullable(Class<T> type, @Nullable Object luaObj)
      throws ConversionException {
    if (luaObj == null) {
      return null;
    }
    return toJava(type, luaObj);
  }

  public final <T> T toJava(Class<T> type, Object luaObj, String name) throws ConversionException {
    checkNotNull(name, "name==null!");
    try {
      return toJava(type, luaObj);
    } catch (ConversionException e) {
      throw new ConversionException(format("Can't convert '%s' argument!", name), e);
    }
  }

  public final <T, I extends Iterable<? super T>> I toJavaIterableFromArray(Class<T> type,
      Object[] luaObjs) throws ConversionException {
    List<T> resultList = new ArrayList<>();
    for (Object luaObj : luaObjs) {
      T javaObj = toJava(type, luaObj);
      resultList.add(javaObj);
    }
    @SuppressWarnings("unchecked")
    I result = (I) resultList;
    return result;
  }

  public final <T, I extends Iterable<? super T>> I toJavaIterable(Class<T> type, Object luaObj)
      throws ConversionException {
    Table table = castToTable(luaObj);
    TableIterable it = new TableIterable(table);
    List<T> resultList = new ArrayList<>();

    for (Map.Entry<Object, Object> e : it) {
      Object elemLuaValue = e.getValue();
      T elemJavaValue = toJava(type, elemLuaValue);
      resultList.add(elemJavaValue);
    }

    @SuppressWarnings("unchecked")
    I result = (I) resultList;
    return result;
  }

  @SuppressWarnings("unchecked")
  public <T> T toJava(Class<T> type, Object luaObj) throws ConversionException {
    checkNotNull(luaObj, "luaObj==null!");
    if (Boolean.class == type) {
      if (luaObj instanceof Boolean) {
        return (T) luaObj;
      }
      throw new ConversionException(format("Can't convert value! Boolean expected, but got: %s",
          luaObj.getClass().getName()));
    }
    if (Integer.class == type) {
      Long longV = Conversions.integerValueOf(luaObj);
      if (longV != null) {
        return (T) Integer.valueOf(longV.intValue());
      }
      throw new ConversionException(format("Can't convert value! Integer expected, but got: %s",
          luaObj.getClass().getName()));
    }
    if (Long.class == type) {
      Long longV = Conversions.integerValueOf(luaObj);
      if (longV != null) {
        return (T) longV;
      }
      throw new ConversionException(
          format("Can't convert value! Long expected, but got: %s", luaObj.getClass().getName()));
    }
    if (Number.class == type) {
      Number numV = Conversions.numericalValueOf(luaObj);
      if (numV != null) {
        return (T) numV;
      }
      throw new ConversionException(
          format("Can't convert value! Number expected, but got: %s", luaObj.getClass().getName()));
    }
    if (String.class == type) {
      ByteString byteStrV = Conversions.stringValueOf(luaObj);
      if (byteStrV != null) {
        return (T) byteStrV.toString();
      }
      throw new ConversionException(
          format("Can't convert value! String expected, but got: %s", luaObj.getClass().getName()));
    }
    throw new IllegalArgumentException(String.format("Unsupported type %s!", type));
  }

}
