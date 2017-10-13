package net.wizardsoflua.lua.data;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.IdentityHashMap;

import javax.annotation.Nullable;

public class TableData {
  private IdentityHashMap<Object, Object> contents;
  private @Nullable String classname;

  public TableData(IdentityHashMap<Object, Object> contents, String classname) {
    this.contents = checkNotNull(contents, "contents==null!");
    this.classname = classname;
  }

  public IdentityHashMap<Object, Object> getContents() {
    return contents;
  }

  public @Nullable String getClassname() {
    return classname;
  }

}
