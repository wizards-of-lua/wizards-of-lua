package net.karneim.luamod.lua.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.Entry;
import net.karneim.luamod.lua.util.table.TableIterable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.sandius.rembulan.Table;

/**
 * Merges {@link NBTTagList}s of type Compound by matching the elements via the value of the
 * specified key.
 * 
 * @author Adrodoc55
 */
public class ValueBasedNbtListMergeStrategy implements NbtListMergeStrategy {
  private final String key;

  public ValueBasedNbtListMergeStrategy(String key) {
    this.key = checkNotNull(key, "key == null!");
  }

  @Override
  public NBTTagList merge(NBTTagList origTagList, Table data) {
    NBTTagList resultTagList = origTagList.copy();
    for (Entry<Object, Object> entry : new TableIterable(data)) {
      Table luaValue = (Table) entry.getValue();
      Object keyValue = luaValue.rawget(key);
      checkNotNull(keyValue, "Expected each value to contain the key: '" + key + "'");
      NBTTagCompound oldValue = getCompoundByValueKey(origTagList, keyValue);
      if (oldValue != null) {
        NBTBase newValue = NBTTagUtil.merge(oldValue, luaValue);
        resultTagList.appendTag(newValue);
      } else {
        NBTBase newValue = NBTTagUtil.fromTable(luaValue);
        resultTagList.appendTag(newValue);
      }
    }
    return resultTagList;
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
