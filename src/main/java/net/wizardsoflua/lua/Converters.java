package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import net.minecraft.util.IStringSerializable;
import net.sandius.rembulan.ByteString;
import net.wizardsoflua.config.ConversionException;
import net.wizardsoflua.config.WolConversions;
import net.wizardsoflua.lua.classes.LuaClass;
import net.wizardsoflua.lua.classes.LuaClasses;

public class Converters extends WolConversions {

  private final ITypes types;
  private final Set<LuaClass<?>> classInstances;

  public Converters(ITypes types, LuaClasses luaClasses) {
    this.types = checkNotNull(types, "types==null!");
    classInstances = luaClasses.load(this);
  }

  public ITypes getTypes() {
    return types;
  }

  public <T> T toJava(Class<T> type, Object luaObj) throws ConversionException {
    checkNotNull(luaObj, "luaObj==null!");
    LuaClass<T> cls = getByJavaClass(type);
    if (cls != null) {
      return cls.toJava(castToTable(luaObj));
    }
    return super.toJava(type, luaObj);
  }

  @SuppressWarnings("unchecked")
  private <T extends ST, ST> LuaClass<T> getByJavaClass(Class<ST> type) {
    if (type == null) {
      return null;
    }
    for (LuaClass<?> classBase : classInstances) {
      if (classBase.getType().equals(type)) {
        return (LuaClass<T>) classBase;
      }
    }
    Class<? super ST> superClass = type.getSuperclass();
    return getByJavaClass(superClass);
  }

  @Override
  public <T> Object toLua(T value) throws ConversionException {
    checkNotNull(value, "value==null!");

    @SuppressWarnings("unchecked")
    Class<T> javaClass = (Class<T>) value.getClass();
    LuaClass<T> cls = getByJavaClass(javaClass);
    if (cls != null) {
      return cls.getLuaInstance(value);
    }
    if (value instanceof Enum) {
      Enum<?> vEnum = (Enum<?>) value;
      if (vEnum instanceof IStringSerializable) {
        return ByteString.of(((IStringSerializable) vEnum).getName());
      }
      return ByteString.of(vEnum.name());
    }
    if (value instanceof IStringSerializable) {
      return ByteString.of(((IStringSerializable) value).getName());
    }
    return super.toLua(value);
  }

}
