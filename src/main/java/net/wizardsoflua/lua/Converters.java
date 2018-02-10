package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

import net.minecraft.util.IStringSerializable;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.wizardsoflua.config.ConversionException;
import net.wizardsoflua.config.WolConversions;
import net.wizardsoflua.lua.classes.JavaLuaClass;
import net.wizardsoflua.lua.classes.LuaClass;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.data.TableData;
import net.wizardsoflua.lua.data.TableDataConverter;
import net.wizardsoflua.lua.module.types.Types;
import net.wizardsoflua.lua.nbt.NbtConverter;

public class Converters extends WolConversions {
  private final LuaClassLoader classLoader;
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
  protected <T> T toJava(Class<T> type, Object luaObject) throws BadArgumentException {
    checkArgument(luaObject != null, "%s expected but got nil", type.getName());
    try {
      Object result = convertTo(type, luaObject);
      return type.cast(result);
    } catch (ClassCastException ex) {
      String expected = getTypes().getTypename(type);
      String actual = getTypes().getTypename(luaObject);
      throw new BadArgumentException(expected, actual);
    }
  }

  @Override
  protected Object convertTo(Class<?> type, Object luaObject)
      throws ClassCastException, BadArgumentException {
    if (luaObject instanceof Table) {
      Table table = (Table) luaObject;
      LuaClass luaClass = classLoader.getLuaClassOf(table);
      if (luaClass instanceof JavaLuaClass) {
        return ((JavaLuaClass<?, ?>) luaClass).getJavaInstance(table);
      }
    }
    return super.convertTo(type, luaObject);
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
