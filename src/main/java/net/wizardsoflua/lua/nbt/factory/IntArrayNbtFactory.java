package net.wizardsoflua.lua.nbt.factory;

import static net.wizardsoflua.lua.table.TableUtils.getLengthIfSequence;
import javax.annotation.Nullable;
import javax.inject.Inject;
import com.google.auto.service.AutoService;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.spell.api.SpellScoped;
import net.wizardsoflua.lua.nbt.NbtConverter;

@AutoService(NbtFactory.class)
@SpellScoped
public class IntArrayNbtFactory extends SingleTypeNbtFactory<NBTTagIntArray, Table> {
  @Inject
  private NbtConverter nbtConverter;

  @Override
  public String getNbtTypeName() {
    return "int_array";
  }

  @Override
  public @Nullable NBTTagIntArray createTypesafe(Table data, @Nullable NBTTagIntArray previous) {
    Integer length = getLengthIfSequence(data);
    if (length == null) {
      return null;
    } else {
      int[] array = new int[length];
      for (int i = 0; i < length; i++) {
        Object value = data.rawget(i + 1);
        NBTTagInt nbt = nbtConverter.convertTo(NBTTagInt.class, value);
        array[i] = nbt.getInt();
      }
      return new NBTTagIntArray(array);
    }
  }
}
