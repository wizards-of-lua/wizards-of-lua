package net.karneim.luamod.gist;

public class GistFile {
  private String filename;
  private String content;

  public GistFile(String filename, String content) {
    this.filename = filename;
    this.content = content;
  }

  public String getFilename() {
    return filename;
  }

  public String getContent() {
    return content;
  }

}
