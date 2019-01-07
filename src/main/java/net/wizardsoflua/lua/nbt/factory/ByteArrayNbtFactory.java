package net.wizardsoflua.lua.nbt.factory;

import static net.wizardsoflua.lua.table.TableUtils.getLengthIfSequence;
import javax.annotation.Nullable;
import javax.inject.Inject;
import com.google.auto.service.AutoService;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.spell.api.SpellScoped;
import net.wizardsoflua.lua.nbt.NbtConverter;

@AutoService(NbtFactory.class)
@SpellScoped
public class ByteArrayNbtFactory extends SingleTypeNbtFactory<NBTTagByteArray, Table> {
  @Inject
  private NbtConverter nbtConverter;

  @Override
  public String getNbtTypeName() {
    return "byte_array";
  }

  @Override
  public @Nullable NBTTagByteArray createTypesafe(Table data, @Nullable NBTTagByteArray previous) {
    Integer length = getLengthIfSequence(data);
    if (length == null) {
      return null;
    } else {
      byte[] array = new byte[length];
      for (int i = 0; i < length; i++) {
        Object value = data.rawget(i + 1);
        NBTTagByte nbt = nbtConverter.convertTo(NBTTagByte.class, value);
        array[i] = nbt.getByte();
      }
      return new NBTTagByteArray(array);
    }
  }
}
