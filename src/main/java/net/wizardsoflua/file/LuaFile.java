package net.wizardsoflua.file;

import java.time.LocalDateTime;

public class LuaFile {
  private final String path;
  private final String name;
  private final String context;
  private final String fileReference;
  private final String content;
  private final boolean exists;
  private final LocalDateTime lastModified;

  public LuaFile(String path, String name, String context, String fileReference, String content,
      boolean exists, LocalDateTime lastModified) {
    this.path = path;
    this.name = name;
    this.context = context;
    this.fileReference = fileReference;
    this.content = content;
    this.exists = exists;
    this.lastModified = lastModified;
  }

  public String getPath() {
    return path;
  }

  public String getName() {
    return name;
  }

  public String getContext() {
    return context;
  }

  public String getFileReference() {
    return fileReference;
  }

  public String getContent() {
    return content;
  }

  public boolean exists() {
    return exists;
  }

  public LocalDateTime getLastModified() {
    return lastModified;
  }

}
