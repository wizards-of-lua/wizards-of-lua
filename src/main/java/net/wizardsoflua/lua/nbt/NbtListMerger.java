package net.wizardsoflua.lua.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.nbt.NBTTagList;
import net.sandius.rembulan.Table;

public class NbtListMerger implements NbtMerger<NBTTagList> {
  private final NbtConverter converter;

  public NbtListMerger(NbtConverter converter) {
    this.converter = checkNotNull(converter, "converter == null!");
  }

  @Override
  public NBTTagList merge(NBTTagList nbt, Object data, String key, String path) {
    if (data instanceof Table) {
      Table table = (Table) data;
      switch (key) {
        case "Items":
        case "Inventory":
          return new ValueBasedNbtListMergeStrategy("Slot", converter).merge(nbt, table, path);
        case "Tags":
          return converter.toNbtList(table);
        default:
          return new IndexBasedNbtListMergeStrategy(converter).merge(nbt, table, path);
      }
    }
    throw converter.conversionException(path, data, "table");
  }
}
