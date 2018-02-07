package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IStringSerializable;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Conversions;
import net.wizardsoflua.config.ConversionException;
import net.wizardsoflua.config.WolConversions;
import net.wizardsoflua.lua.classes.LuaClass;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.classes.entity.PlayerClass;
import net.wizardsoflua.lua.data.TableData;
import net.wizardsoflua.lua.data.TableDataConverter;
import net.wizardsoflua.lua.nbt.NbtConverter;

public class Converters extends WolConversions {
  private final LuaClassLoader luaClassLoader;
  private final EnumConverter enumConverter = new EnumConverter();
  private final NbtConverter nbtConverter;
  private final TableDataConverter tableDataConverter = new TableDataConverter(this);

  public Converters(LuaClassLoader luaClassLoader) {
    this.luaClassLoader = requireNonNull(luaClassLoader, "luaClassLoader == null!");
    nbtConverter = new NbtConverter(getTypes());
  }

  public ITypes getTypes() {
    return luaClassLoader.getTypes();
  }

  public NbtConverter getNbtConverter() {
    return nbtConverter;
  }

  public void replacePlayerInstance(EntityPlayerMP player) {
    Object luaClass = luaClassLoader.getByJavaClassOrSuperClass(EntityPlayerMP.class);
    // TODO can we replace this ugly casting stuff with something more elegant?
    if (luaClass instanceof PlayerClass) {
      PlayerClass pc = (PlayerClass) luaClass;
      pc.replaceDelegate(player);
    } else {
      throw new IllegalStateException(String.format(
          "Expected luaClass to be instanceof %s, but was %s", PlayerClass.class, luaClass));
    }
  }

  @Override
  public <T> T toJava(Class<T> type, Object luaObj) throws ConversionException {
    checkNotNull(luaObj, "luaObj==null!");
    // FIXME Adrodoc55 07.02.2018: Sollten wir nicht eher das luaObj nach seiner Klasse fragen?
    LuaClass<T, ?> cls = luaClassLoader.getByJavaClass(type);
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
    LuaClass<? super T, ?> cls = luaClassLoader.getByJavaClassOrSuperClass(javaClass);
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
