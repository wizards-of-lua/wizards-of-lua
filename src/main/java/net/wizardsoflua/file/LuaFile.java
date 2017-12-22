package net.wizardsoflua.file;

public class LuaFile {
  private final String path;
  private final String name;
  private final String fileReference;
  private final String content;

  public LuaFile(String path, String name, String fileReference, String content) {
    this.path = path;
    this.name = name;
    this.fileReference = fileReference;
    this.content = content;
  }

  public String getPath() {
    return path;
  }

  public String getName() {
    return name;
  }

  public String getFileReference() {
    return fileReference;
  }

  public String getContent() {
    return content;
  }

}
