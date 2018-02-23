package net.wizardsoflua.gist;

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

  public @Nullable GistFile getFile(String filename) {
    for (GistFile gistFile : files) {
      if (filename.equals(gistFile.filename)) {
        return gistFile;
      }
    }
    return null;
  }

}
