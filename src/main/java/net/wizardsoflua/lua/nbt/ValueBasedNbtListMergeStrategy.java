package net.wizardsoflua.lua.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.table.TableIterable;

/**
 * Merges {@link NBTTagList}s of type Compound by matching the elements via the value of the
 * specified key.
 *
 * @author Adrodoc55
 */
public class ValueBasedNbtListMergeStrategy implements NbtListMergeStrategy {
  private final String key;
  private final NbtConverter converter;

  public ValueBasedNbtListMergeStrategy(String key, NbtConverter converter) {
    this.key = checkNotNull(key, "key == null!");
    this.converter = checkNotNull(converter, "converter == null!");
  }

  @Override
  public NBTTagList merge(NBTTagList nbt, Table data, String path) {
    NBTTagList result = nbt.copy();
    for (Entry<Object, Object> entry : new TableIterable(data)) {
      Table luaValue = (Table) entry.getValue();
      Object keyValue = luaValue.rawget(key);
      checkNotNull(keyValue, "Expected each value to contain the key: '" + key + "'");
      NBTTagCompound oldValue = getCompoundByValueKey(nbt, keyValue);
      if (oldValue != null) {
        NBTBase newValue = converter.merge(oldValue, luaValue, path + "[" + keyValue + "]");
        result.appendTag(newValue);
      } else {
        NBTBase newValue = converter.toNbtCompound(luaValue);
        result.appendTag(newValue);
      }
    }
    return result;
  }

  private @Nullable NBTTagCompound getCompoundByValueKey(NBTTagList compoundList, Object keyValue) {
    checkNotNull(keyValue, "keyValue == null!");
    for (int i = 0; i < compoundList.tagCount(); ++i) {
      NBTTagCompound compound = compoundList.getCompoundTagAt(i);
      NBTBase nbtKeyValue = compound.getTag(key);
      checkNotNull(nbtKeyValue, "Expected each NBT value to contain the key: '" + key + "'");
      if (nbtKeyValue.equals(keyValue)) {
        return compound;
      }
    }
    return null;
  }
}
