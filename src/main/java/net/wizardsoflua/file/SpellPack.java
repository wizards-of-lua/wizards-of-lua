package net.wizardsoflua.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SpellPack {

  private String fileReference;
  private File jarFile;

  public SpellPack(String fileReference, File jarFile) throws IOException {
    this.fileReference = fileReference;
    this.jarFile = jarFile;
  }

  public String getFileReference() {
    return fileReference;
  }

  public InputStream open() throws FileNotFoundException {
    return new FileInputStream(jarFile);
  }

  public long getSize() {
    return jarFile.length();
  }

}
