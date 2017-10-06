package net.wizardsoflua.lua.table;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.wizardsoflua.config.WolConversions;

public class TableUtils {

  private static final Pattern LUA_IDENTIFIER = Pattern.compile("^[_a-zA-Z][_a-zA-Z0-9]*$");

  private TableUtils() {}

  public static <T> T getAs(Class<T> type, Table table, String key) {
    Object value = table.rawget(key);
    return WolConversions.toJava(type, value, key);
  }

  public static <T> Optional<T> getAsOptional(Class<T> type, Table table, String key) {
    Object value = table.rawget(key);
    return WolConversions.toJavaOptional(type, value);
  }

  public static @Nullable <T> T getAsNullable(Class<T> type, Table table, String key) {
    Object value = table.rawget(key);
    return WolConversions.toJavaNullable(type, value, key);
  }

  public static void writeTo(PrintWriter out, Table table) {
    writeTo(out, table, "");
  }

  public static void writeTo(PrintWriter out, Table table, String indent) {
    String lineIndent = indent + "  ";
    out.write("{ ");
    TableIterable it = new TableIterable(table);
    String delimitter = "\n";
    for (Map.Entry<Object, Object> entry : it) {
      out.write(delimitter);
      out.write(lineIndent);
      Object key = entry.getKey();
      out.write(toOutKey(key));
      out.write("=");
      Object value = entry.getValue();
      if (value instanceof Table) {
        writeTo(out, (Table) value, lineIndent);
      } else {
        out.write(toOutValue(value));
      }
      delimitter = ",\n";
    }
    out.write(indent);
    out.write(" }");
  }

  private static String toOutKey(Object key) {
    if (key instanceof Long) {
      String result = ((Long) key).toString();
      return result;
    } else if ((key instanceof ByteString) || (key instanceof String)) {
      String result = String.valueOf(key);
      if (!isLuaIdentifier(result)) {
        return "[\"" + result + "\"]";
      }
      return result;
    } else {
      throw new IllegalArgumentException(String.format("table contains an invalid key '%s'!", key));
    }
  }

  private static String toOutValue(Object value) {
    if (value instanceof Boolean) {
      String result = ((Boolean) value).toString();
      return result;
    } else if (value instanceof Long) {
      String result = ((Long) value).toString();
      return result;
    } else if ((value instanceof ByteString) || (value instanceof String)) {
      String result = "\"" + escapeQuotes(String.valueOf(value)) + "\"";
      return result;
    } else {
      throw new IllegalArgumentException(
          String.format("table contains an invalid value '%s'!", value));
    }
  }

  private static String escapeQuotes(String str) {
    return str.replace("\"", "\\\"");
  }

  private static boolean isLuaIdentifier(String text) {
    return LUA_IDENTIFIER.matcher(text).matches();
  }

}
