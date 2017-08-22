package net.wizardsoflua.lua.table;

import net.sandius.rembulan.Table;

public interface TableBuilder {

  TableBuilder add(Object key, Object value);

  TableBuilder setMetatable(Table table);

  Table build();

}
