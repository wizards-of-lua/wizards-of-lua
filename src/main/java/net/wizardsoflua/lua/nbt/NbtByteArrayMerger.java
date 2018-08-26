package net.wizardsoflua.lua.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.nbt.NBTTagByteArray;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.Table;

public class NbtByteArrayMerger implements NbtMerger<NBTTagByteArray> {

  private final NbtConverter converter;

  public NbtByteArrayMerger(NbtConverter converter) {
    this.converter = checkNotNull(converter, "converter == null!");
  }

  @Override
  public NBTTagByteArray merge(NBTTagByteArray nbt, Object data, String key, String path) {
    if (data instanceof Table) {
      Table table = (Table) data;
      NBTTagByteArray result = (NBTTagByteArray) nbt.copy();
      byte[] byteArray = nbt.getByteArray();
      for (int i = 0; i < byteArray.length; ++i) {
        Object newLuaValue = table.rawget(i + 1);
        if (newLuaValue != null) {
          byte newNbtValue = Conversions.integerValueOf(newLuaValue).byteValue();
          result.getByteArray()[i] = newNbtValue;
        }
      }
      return result;
    }
    throw converter.conversionException(path, data, "table");
  }

}
