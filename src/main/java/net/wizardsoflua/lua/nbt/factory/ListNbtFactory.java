package net.wizardsoflua.lua.nbt.factory;

import static net.wizardsoflua.lua.table.TableUtils.getLengthIfSequence;
import javax.annotation.Nullable;
import javax.inject.Inject;
import com.google.auto.service.AutoService;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.spell.api.SpellScoped;
import net.wizardsoflua.lua.nbt.NbtConverter;

@AutoService(NbtFactory.class)
@SpellScoped
public class ListNbtFactory extends AbstractNbtFactory<NBTTagList, Table> {
  @Inject
  private NbtConverter nbtConverter;

  @Override
  public @Nullable NBTTagList create(Table data, @Nullable NBTTagList previous) {
    Integer length = getLengthIfSequence(data);
    if (length == null) {
      return null;
    } else {
      NBTTagList result = new NBTTagList();
      for (int i = 1; i <= length; i++) {
        Object value = data.rawget(i);
        NBTBase nbt = toNbt(value, previous);
        result.appendTag(nbt);
      }
      return result;
    }
  }

  private NBTBase toNbt(Object value, @Nullable NBTTagList previous) {
    if (previous != null) {
      int previousType = previous.getTagType();
      Class<? extends NBTBase> previousClass = NbtConverter.getNbtClassById(previousType);
      NbtFactory<NBTBase, ?> factory = nbtConverter.getFactory(previousClass);
      NBTBase nbt = factory.tryCreate(value, null);
      if (nbt != null) {
        return nbt;
      }
    }
    return nbtConverter.toNbt(value);
  }
}
