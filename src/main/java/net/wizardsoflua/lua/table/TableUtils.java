package net.wizardsoflua.lua.table;

import net.sandius.rembulan.Table;
import java.util.Map.Entry;

public class TableUtils {
  public static String toString(Table table) {
    StringBuilder buf = new StringBuilder();
    TableIterable it = new TableIterable(table);
    for (Entry<Object, Object> entry : it) {
      if (buf.length() > 0) {
        buf.append(", ");
      }
      buf.append(entry.getKey());
      buf.append("=");
      if (entry.getValue() instanceof Table) {
        buf.append(toString((Table)entry.getValue()));
      } else {
        buf.append(entry.getValue());
      }
    }
    return "{" + buf.toString() + "}";
  }
}
