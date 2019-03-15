package net.wizardsoflua.wol.file;

public class FileRef {
  public final String localPath;
  public final String fullPath;

  public FileRef(String localPath, String fullPath) {
    this.localPath = localPath;
    this.fullPath = fullPath;
  }
}
