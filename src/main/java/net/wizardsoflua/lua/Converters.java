package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IStringSerializable;
import net.sandius.rembulan.ByteString;
import net.wizardsoflua.config.ConversionException;
import net.wizardsoflua.config.WolConversions;
import net.wizardsoflua.lua.classes.LuaClass;
import net.wizardsoflua.lua.classes.LuaClasses;
import net.wizardsoflua.lua.classes.entity.PlayerClass;
import net.wizardsoflua.lua.data.TableData;
import net.wizardsoflua.lua.data.TableDataConverter;
import net.wizardsoflua.lua.nbt.NbtConverter;

public class Converters extends WolConversions {
  private final ITypes types;
  private final NbtConverter nbtConverter;
  private final Set<LuaClass<?, ?>> classInstances;
  private final TableDataConverter tableDataConverter = new TableDataConverter(this);

  public Converters(ITypes types, LuaClasses luaClasses) {
    this.types = checkNotNull(types, "types==null!");
    nbtConverter = new NbtConverter(types);
    classInstances = luaClasses.load(this);
  }

  public ITypes getTypes() {
    return types;
  }

  public NbtConverter getNbtConverter() {
    return nbtConverter;
  }

  public void replacePlayerInstance(EntityPlayerMP player) {
    Object luaClass = getByJavaClass(EntityPlayerMP.class);
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
    LuaClass<T, ?> cls = getByJavaClass(type);
    if (cls != null) {
      return cls.getJavaInstance(castToTable(luaObj));
    }
    return super.toJava(type, luaObj);
  }

  @SuppressWarnings("unchecked")
  private <T extends ST, ST> LuaClass<T, ?> getByJavaClass(Class<ST> type) {
    if (type == null) {
      return null;
    }
    for (LuaClass<?, ?> classBase : classInstances) {
      if (classBase.getJavaClass().equals(type)) {
        return (LuaClass<T, ?>) classBase;
      }
    }
    Class<? super ST> superClass = type.getSuperclass();
    return getByJavaClass(superClass);
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
    LuaClass<T, ?> cls = getByJavaClass(javaClass);
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
