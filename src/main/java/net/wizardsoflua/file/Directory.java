package net.wizardsoflua.file;

import java.util.List;

public class Directory {
  private final String path;
  private final String name;
  private final List<String> children;

  public Directory(String path, String name, List<String> children) {
    super();
    this.path = path;
    this.name = name;
    this.children = children;
  }

  public List<String> getChildren() {
    return children;
  }

  public String getPath() {
    return path;
  }

  public String getName() {
    return name;
  }

}
