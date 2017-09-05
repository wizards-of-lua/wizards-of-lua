package net.wizardsoflua.lua.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.sandius.rembulan.Table;

/**
 * Merges {@link NBTTagList}s by matching the elements via their list index.
 * 
 * @author Adrodoc55
 */
public class IndexBasedNbtListMergeStrategy implements NbtListMergeStrategy {
  @Override
  public NBTTagList merge(NBTTagList origTagList, Table data) {
    NBTTagList resultTagList = origTagList.copy();
    for (int i = 0; i < origTagList.tagCount(); ++i) {
      NBTBase oldValue = origTagList.get(i);
      Object luaValue = data.rawget(i + 1);
      if (luaValue != null) {
        String key = null; // There are no NBTTagLists of type NBTTagList so key can be null
        NBTBase newValue = NbtConverter.merge(key, oldValue, luaValue);
        resultTagList.set(i, newValue);
      }
    }
    return resultTagList;
  }
}
