package net.wizardsoflua.gist;

public class GistFile {
  public final String filename;
  public final String content;

  public GistFile(String filename, String content) {
    this.filename = filename;
    this.content = content;
  }
  
}
