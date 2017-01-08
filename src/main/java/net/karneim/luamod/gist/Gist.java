package net.karneim.luamod.gist;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class Gist {
  private final List<GistFile> files = new ArrayList<GistFile>();

  public Gist() {}

  public List<GistFile> getFiles() {
    return files;
  }

  public void add(GistFile file) {
    files.add(file);
  }

  public @Nullable GistFile getFile(String name) {
    for (GistFile gistFile : files) {
      if (gistFile.getFilename().equals(name)) {
        return gistFile;
      }
    }
    return null;
  }

}
