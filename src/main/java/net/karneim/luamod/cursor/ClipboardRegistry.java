package net.karneim.luamod.cursor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

public class ClipboardRegistry {
  private final Map<UUID, Clipboard> clipboards = new HashMap<UUID, Clipboard>();

  public Clipboard get(EntityPlayer owner) {
    UUID key = owner.getUniqueID();
    Clipboard result = clipboards.get(key);
    if (result == null) {
      result = new Clipboard();
      clipboards.put(key, result);
    }
    return result;
  }

}
