package net.wizardsoflua.lua.nbt;

import net.minecraft.nbt.NBTTagList;
import net.sandius.rembulan.Table;

public class NbtListMerger implements NbtMerger<NBTTagList> {
  @Override
  public NBTTagList merge(NBTTagList nbt, Object data, String key, String path) {
    if (data instanceof Table) {
      Table table = (Table) data;
      switch (key) {
        case "Items":
        case "Inventory":
          return new ValueBasedNbtListMergeStrategy("Slot").merge(nbt, table, path);
        case "Tags":
          return NbtConverter.toNbtList(table);
        default:
          return new IndexBasedNbtListMergeStrategy().merge(nbt, table, path);
      }
    }
    throw NbtMerger.conversionException(path, data, "Table");
  }
}
