package net.wizardsoflua.lua.nbt;

import static com.google.common.base.Preconditions.checkNotNull;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagList;
import net.sandius.rembulan.Table;

/**
 * Merges {@link NBTTagList}s by matching the elements via their list index.
 *
 * @author Adrodoc55
 */
public class IndexBasedNbtListMergeStrategy implements NbtListMergeStrategy {
  private final NbtConverter converter;

  public IndexBasedNbtListMergeStrategy(NbtConverter converter) {
    this.converter = checkNotNull(converter, "converter == null!");
  }

  @Override
  public NBTTagList merge(NBTTagList nbt, Table data, String path) {
    NBTTagList result = nbt.copy();
    for (int i = 0; i < nbt.size(); ++i) {
      INBTBase oldNbtValue = nbt.get(i);
      Object newLuaValue = data.rawget(i + 1);
      if (newLuaValue != null) {
        String key = String.valueOf(i);
        INBTBase newNbtValue = converter.merge(oldNbtValue, newLuaValue, key, path + "[" + i + "]");
        result.set(i, newNbtValue);
      }
    }
    return result;
  }
}
