package net.wizardsoflua.lua.data;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.sandius.rembulan.util.TraversableHashMap;

public class TableData {
  private TraversableHashMap<Object, Object> contents;
  private @Nullable String classname;

  public TableData(TraversableHashMap<Object, Object> contents, String classname) {
    this.contents = checkNotNull(contents, "contents==null!");
    this.classname = classname;
  }

  public TraversableHashMap<Object, Object> getContents() {
    return contents;
  }

  public @Nullable String getClassname() {
    return classname;
  }

}
