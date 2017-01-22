package net.karneim.luamod.lua.event;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.DynamicTable;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;

public class GenericLuaEventWrapper extends EventWrapper<Object> {
  public GenericLuaEventWrapper(@Nullable Object delegate, String name) {
    super(delegate, name);
  }

  @Override
  protected void addProperties(DynamicTable.Builder builder) {
    super.addProperties(builder);
    builder.add("content", copyOf(delegate));
  }

  private Object copyOf(Object content) {
    if (content instanceof Table) {
      Table table = (Table) content;
      return copyOf(table);
    }
    if (content instanceof ByteString) {
      return content;
    }
    if (content instanceof String) {
      return content;
    }
    if (content instanceof Number) {
      return content;
    }
    if (content instanceof Boolean) {
      return content;
    }
    throw new IllegalArgumentException(
        "Unsupported event content type: " + content.getClass().getSimpleName());
  }

  private Table copyOf(Table source) {
    if (source instanceof DynamicTable) {
      DynamicTable it = (DynamicTable) source;
      return it;
    } else {
      Table result = DefaultTable.factory().newTable();
      Object key = source.initialKey();
      while (key != null) {
        Object srcValue = source.rawget(key);
        Object copy = copyOf(srcValue);
        result.rawset(key, srcValue);
        key = source.successorKeyOf(key);
      }
      return result;
    }
  }

}
