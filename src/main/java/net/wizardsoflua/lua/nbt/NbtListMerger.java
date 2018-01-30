package net.wizardsoflua.lua.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.nbt.NBTTagList;
import net.sandius.rembulan.Table;

/**
 * @author Adrodoc55
 * @see
 *      <ul>
 *      <li><a href=
 *      "https://bugs.mojang.com/browse/MC-112253">https://bugs.mojang.com/browse/MC-112253</a></li>
 *      <li><a href=
 *      "http://skylinerw.com/compendium2/compendium.html">http://skylinerw.com/compendium2/compendium.html</a></li>
 *      <li><a href=
 *      "https://minecraft-de.gamepedia.com/Blockobjektdaten">https://minecraft-de.gamepedia.com/Blockobjektdaten</a></li>
 *      <li><a href=
 *      "https://minecraft-de.gamepedia.com/Gegenstandsdaten">https://minecraft-de.gamepedia.com/Gegenstandsdaten</a></li>
 *      <li><a href=
 *      "https://minecraft-de.gamepedia.com/Kreaturdaten">https://minecraft-de.gamepedia.com/Kreaturdaten</a></li>
 *      <li><a href=
 *      "https://minecraft-de.gamepedia.com/Objektdaten">https://minecraft-de.gamepedia.com/Objektdaten</a></li>
 *      <li><a href=
 *      "https://minecraft-de.gamepedia.com/Spielerdaten">https://minecraft-de.gamepedia.com/Spielerdaten</a></li>
 *      </ul>
 */
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
        case "ActiveEffects":
          return new ValueBasedNbtListMergeStrategy("Id", converter).merge(nbt, table, path);
        case "EnderItems":
        case "Inventory":
        case "Items":
          return new ValueBasedNbtListMergeStrategy("Slot", converter).merge(nbt, table, path);
        case "AttributeModifiers":
        case "Tags":
          return converter.toNbtList(table);
        default:
          return new IndexBasedNbtListMergeStrategy(converter).merge(nbt, table, path);
      }
    }
    throw converter.conversionException(path, data, "table");
  }
}
