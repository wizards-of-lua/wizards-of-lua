package net.karneim.luamod.cursor;

import java.util.HashMap;
import java.util.Map;

public class Clipboard {
  private final Map<String, Snapshot> snapshots = new HashMap<String, Snapshot>();

  public void put(String name, Snapshot snapshot) {
    snapshots.put(name, snapshot);
  }

  public Snapshot remove(String name) {
    return snapshots.remove(name);
  }

  public Snapshot get(String name) {
    return snapshots.get(name);
  }

  public void clear() {
    snapshots.clear();
  }

  public boolean contains(String name) {
    return snapshots.containsKey(name);
  }
}
