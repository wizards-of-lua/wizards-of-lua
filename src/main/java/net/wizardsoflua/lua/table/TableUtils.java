package net.wizardsoflua.lua.table;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.wizardsoflua.config.ConversionException;
import net.wizardsoflua.lua.BadArgumentException;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.module.types.Types;

public class TableUtils {

  private static final Pattern LUA_IDENTIFIER = Pattern.compile("^[_a-zA-Z][_a-zA-Z0-9]*$");

  private static final Converters CONVERSION = new Converters(TableUtils::getTypes);
  private static final Types TYPES = new Types(TableUtils::getConversion);

  private static Converters getConversion() {
    return CONVERSION;
  }

  private static Types getTypes() {
    return TYPES;
  }

  private TableUtils() {}

  public static <T> Optional<T> getAsOptional(Class<T> type, Table table, String key) {
    return ofNullable(getAsNullable(type, table, key));
  }

  public static @Nullable <T> T getAsNullable(Class<T> type, Table table, String key) {
    Object value = table.rawget(key);
    if (value == null) {
      return null;
    }
    return getAs(type, table, key);
  }

  public static <T> T getAs(Class<T> type, Table table, String key) {
    Object value = table.rawget(key);
    try {
      return CONVERSION.toJava(type, value, key);
    } catch (BadArgumentException ex) {
      throw new ConversionException(format("Can't convert value '%s'! %s expected, but got: %s",
          key, type.getName(), value.getClass().getName()), ex);
    }
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
