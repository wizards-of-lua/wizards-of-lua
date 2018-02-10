package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

import net.minecraft.util.IStringSerializable;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.wizardsoflua.config.ConversionException;
import net.wizardsoflua.config.WolConversions;
import net.wizardsoflua.lua.classes.JavaLuaClass;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.data.TableData;
import net.wizardsoflua.lua.data.TableDataConverter;
import net.wizardsoflua.lua.module.types.Types;
import net.wizardsoflua.lua.nbt.NbtConverter;

public class Converters extends WolConversions {
  private final LuaClassLoader classLoader;
  private final EnumConverter enumConverter = new EnumConverter();
  private final NbtConverter nbtConverter;
  private final TableDataConverter tableDataConverter;

  public Converters(LuaClassLoader classLoader) {
    this.classLoader = requireNonNull(classLoader, "classLoader == null!");
    nbtConverter = new NbtConverter(classLoader);
    tableDataConverter = new TableDataConverter(classLoader);
  }

  public Types getTypes() {
    return classLoader.getTypes();
  }

  public NbtConverter getNbtConverter() {
    return nbtConverter;
  }

  @Override
  public <T> T toJava(Class<T> type, Object luaObj) throws ConversionException {
    checkNotNull(luaObj, "luaObj==null!");
    // FIXME Adrodoc55 07.02.2018: Sollten wir nicht eher das luaObj nach seiner Klasse fragen?
    JavaLuaClass<T, ?> cls = classLoader.getLuaClassForJavaClass(type);
    if (cls != null) {
      return cls.getJavaInstance(castToTable(luaObj));
    }
    if (Enum.class.isAssignableFrom(type)) {
      ByteString byteStrV = Conversions.stringValueOf(luaObj);
      String name = byteStrV.toString();
      T result = enumConverter.toJava(type, name);
      if (result != null) {
        return (T) result;
      }
    }
    return super.toJava(type, luaObj);
  }

  @Override
  public <T> Object toLua(T value) throws ConversionException {
    checkNotNull(value, "value==null!");

    if (value instanceof TableData) {
      TableData data = (TableData) value;
      return tableDataConverter.toLua(data);
    }

    @SuppressWarnings("unchecked")
    Class<T> javaClass = (Class<T>) value.getClass();
    JavaLuaClass<? super T, ?> cls = classLoader.getLuaClassForJavaClassRecursively(javaClass);
    if (cls != null) {
      return cls.getLuaInstance(value);
    }
    if (value instanceof Enum) {
      Enum<?> vEnum = (Enum<?>) value;
      if (vEnum instanceof IStringSerializable) {
        return ByteString.of(((IStringSerializable) vEnum).getName());
      }
      Object result = enumConverter.toLua(vEnum);
      if (result != null) {
        return result;
      }
      return ByteString.of(vEnum.name());
    }
    if (value instanceof IStringSerializable) {
      return ByteString.of(((IStringSerializable) value).getName());
    }
    return super.toLua(value);
  }
}
