package net.karneim.luamod.cursor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

public class Snapshots {
  private final Map<String, Snapshot> snapshots = new HashMap<String, Snapshot>();

  public String registerSnapshot(Snapshot snapshot) {
    Preconditions.checkNotNull(snapshot, "snapshot==null!");
    String id = getRegisteredIdOf(snapshot);
    if (id == null) {
      id = UUID.randomUUID().toString();
      snapshots.put(id, snapshot);
    }
    return id;
  }

  public Snapshot getSnapshot(String id) {
    Preconditions.checkNotNull(id, "id==null!");
    Snapshot result = snapshots.get(id);
    return result;
  }

  private @Nullable String getRegisteredIdOf(Snapshot snapshot) {
    for (Entry<String, Snapshot> entry : snapshots.entrySet()) {
      if (entry.getValue() == snapshot) {
        return entry.getKey();
      }
    }
    return null;
  }

}
