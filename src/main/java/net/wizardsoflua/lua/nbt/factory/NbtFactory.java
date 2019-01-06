package net.wizardsoflua.lua.nbt.factory;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.sandius.rembulan.ConversionException;
import net.wizardsoflua.lua.nbt.NbtConverter;

public interface NbtFactory<NBT extends NBTBase> {
  @SuppressWarnings({"rawtypes", "unchecked"})
  static Class<NbtFactory<?>> getClassWithWildcards() {
    return (Class) NbtFactory.class;
  }

  String getNbtTypeName();

  String getLuaTypeName();

  Class<NBT> getNbtClass();

  @Nullable
  NBT create(Object data, @Nullable NBT previousValue);

  default NBT convert(Object value) throws ConversionException {
    NBT nbt = create(value, null);
    if (nbt != null) {
      return nbt;
    } else {
      throw new ConversionException("Cannot convert " + NbtConverter.formatLuaValue(value) + " to "
          + getNbtTypeName() + " NBT: expected " + getLuaTypeName() + " but got "
          + types.getLuaTypeNameOfLuaObject(value));
    }
  }

}
