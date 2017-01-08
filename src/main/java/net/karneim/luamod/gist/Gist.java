package net.karneim.luamod.gist;

import java.util.ArrayList;
import java.util.List;

public class Gist {
  private final List<GistFile> files = new ArrayList<GistFile>();

  public Gist() {
  }

  public List<GistFile> getFiles() {
    return files;
  }

  public void add(GistFile file) {
    files.add(file);
  }

}
